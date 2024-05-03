package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.factory.PosGuiData
import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.block.BlockMachine.Companion.IS_PIPE
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.SYNC_MTE_TRAIT
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_CONNECTIONS
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_FRONT_FACING
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_INPUT_MODE
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_OUTPUT_MODE
import com.github.trcdevelopers.clayium.api.metatileentity.interfaces.ISyncedTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.IPipeConnectable
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.*
import com.github.trcdevelopers.clayium.common.tileentity.AutoIoHandler
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

abstract class MetaTileEntity(
    val metaTileEntityId: ResourceLocation,
    val tier: Int,
    val validInputModes: List<MachineIoMode>,
    val validOutputModes: List<MachineIoMode>,
    val translationKey: String,
    val faceTexture: ResourceLocation? = null,
) : ISyncedTileEntity, IGuiHolder<PosGuiData>, IPipeConnectable {

    var holder: MetaTileEntityHolder? = null
    val world: World? get() = holder?.world
    val pos: BlockPos? get() = holder?.pos

    protected val mteTraits = mutableMapOf<String, MTETrait>()
    private val traitByNetworkId = Int2ObjectOpenHashMap<MTETrait>()

    abstract val importItems: IItemHandlerModifiable
    abstract val exportItems: IItemHandlerModifiable
    abstract val itemInventory: IItemHandler
    abstract val autoIoHandler: AutoIoHandler

    abstract fun createMetaTileEntity(): MetaTileEntity

    private val _inputModes = MutableList(6) { MachineIoMode.NONE }
    private val _outputModes = MutableList(6) { MachineIoMode.NONE }
    private val _connectionsCache = BooleanArray(6)
    val inputModes get() = _inputModes.toList()
    val outputModes get() = _outputModes.toList()
    val connectionsCache get() = _connectionsCache.copyOf()

    open val hasFrontFacing = true
    var frontFacing = EnumFacing.NORTH
        protected set

    fun addMetaTileEntityTrait(trait: MTETrait) {
        mteTraits.put(trait.name, trait)
    }

    fun markDirty() = holder?.markDirty()

    fun update() {
        if (world?.isRemote == true) return
    }

    open fun writeToNBT(data: NBTTagCompound) {
        data.setByte("frontFacing", frontFacing.index.toByte())
        data.setByteArray("inputModes", ByteArray(6) { _inputModes[it].id.toByte() })
        data.setByteArray("outputModes", ByteArray(6) { _outputModes[it].id.toByte() })
        CUtils.writeItems(importItems, "importInventory", data)
        CUtils.writeItems(exportItems, "exportInventory", data)
    }

    open fun readFromNBT(data: NBTTagCompound) {
        frontFacing = EnumFacing.byIndex(data.getByte("frontFacing").toInt())
        data.getByteArray("inputModes").forEachIndexed { i, id -> _inputModes[i] = MachineIoMode.entries[id.toInt()] }
        data.getByteArray("outputModes").forEachIndexed { i, id -> _outputModes[i] = MachineIoMode.entries[id.toInt()] }
        CUtils.readItems(importItems, "importInventory", data)
        CUtils.readItems(exportItems, "exportInventory", data)
        EnumFacing.entries.forEach(this::refreshConnection)
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        buf.writeByte(frontFacing.index)
        for (i in 0..5) {
            buf.writeByte(_inputModes[i].id)
            buf.writeByte(_outputModes[i].id)
            buf.writeBoolean(_connectionsCache[i])
        }
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        frontFacing = EnumFacing.byIndex(buf.readByte().toInt())
        for (i in 0..5) {
            _inputModes[i] = MachineIoMode.entries[buf.readByte().toInt()]
            _outputModes[i] = MachineIoMode.entries[buf.readByte().toInt()]
            _connectionsCache[i] = buf.readBoolean()
        }
    }

    override fun writeCustomData(discriminator: Int, dataWriter: PacketBuffer.() -> Unit) {
        this.holder?.writeCustomData(discriminator, dataWriter)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_FRONT_FACING -> frontFacing = EnumFacing.byIndex(buf.readByte().toInt())
            UPDATE_INPUT_MODE -> _inputModes[buf.readByte().toInt()] = MachineIoMode.entries[buf.readByte().toInt()]
            UPDATE_OUTPUT_MODE -> _outputModes[buf.readByte().toInt()] = MachineIoMode.entries[buf.readByte().toInt()]
            UPDATE_CONNECTIONS -> _connectionsCache[buf.readByte().toInt()] = buf.readBoolean()
            SYNC_MTE_TRAIT -> {
                traitByNetworkId[buf.readVarInt()]?.receiveCustomData(buf.readVarInt(), buf)
            }
        }
    }

    fun writeMteData(mteTrait: MTETrait, discriminator: Int, dataWriter: PacketBuffer.() -> Unit) {
        writeCustomData(SYNC_MTE_TRAIT) {
            writeVarInt(mteTrait.networkId)
            writeVarInt(discriminator)
            dataWriter()
        }
    }

    fun onToolClick(
        toolType: ItemClayConfigTool.ToolType ,player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float
    ) {
        when (toolType) {
            PIPING -> {
                val world = world ?: return
                val pos = pos ?: return
                world.setBlockState(pos, world.getBlockState(pos).cycleProperty(IS_PIPE))
            }
            INSERTION -> toggleInput(clickedSide)
            EXTRACTION -> toggleOutput(clickedSide)
            ROTATION -> rotate(clickedSide)
            FILTER_REMOVER -> TODO()
        }
    }

    private fun rotate(side: EnumFacing) {
        if (side.axis.isVertical) return
        if (frontFacing == side) {
            frontFacing = frontFacing.opposite
            val oldInputs = _inputModes.toList()
            val oldOutputs = _outputModes.toList()
            for (side in EnumFacing.HORIZONTALS) {
                val rotatedSide = side.opposite
                _inputModes[rotatedSide.index] = oldInputs[side.index]
                _outputModes[rotatedSide.index] = oldOutputs[side.index]
            }
        } else {
            while (frontFacing != side) {
                val oldInputs = _inputModes.toList()
                val oldOutputs = _outputModes.toList()
                frontFacing = frontFacing.rotateY()
                for (side in EnumFacing.HORIZONTALS) {
                    val rotatedSide = side.rotateY()
                    _inputModes[rotatedSide.index] = oldInputs[side.index]
                    _outputModes[rotatedSide.index] = oldOutputs[side.index]
                }
            }
        }
    }

    override fun getInput(side: EnumFacing) = _inputModes[side.index]
    override fun getOutput(side: EnumFacing) = _outputModes[side.index]

    protected fun toggleInput(side: EnumFacing) {
        val current = _inputModes[side.index]
        _inputModes[side.index] = validInputModes[(validInputModes.indexOf(current) + 1) % validInputModes.size]
        writeCustomData(UPDATE_INPUT_MODE) {
            writeByte(side.index)
            writeByte(_inputModes[side.index].id)
        }
    }

    protected fun toggleOutput(side: EnumFacing) {
        val current = _outputModes[side.index]
        _outputModes[side.index] = validOutputModes[(validOutputModes.indexOf(current) + 1) % validOutputModes.size]
        writeCustomData(UPDATE_OUTPUT_MODE) {
            writeByte(side.index)
            writeByte(_outputModes[side.index].id)
        }
    }

    protected fun refreshConnection(side: EnumFacing) {
        val neighborPos = pos?.offset(side) ?: return
        val mte = CUtils.getMetaTileEntity(world, neighborPos)
        val i = side.index
        if (mte != null) {
            _connectionsCache[i] = canConnectToMte(mte, side)
        } else {
            val neighbor = world?.getTileEntity(neighborPos) ?: return
            _connectionsCache[i] = canConnectTo(neighbor, side)
        }
        writeCustomData(UPDATE_CONNECTIONS) {
            writeByte(i)
            writeBoolean(_connectionsCache[i])
        }
    }

    protected fun canConnectToMte(neighbor: MetaTileEntity, side: EnumFacing): Boolean {
        val i = side.index
        return (this._inputModes[i] != MachineIoMode.NONE && neighbor._outputModes[i] != MachineIoMode.NONE
                || this._outputModes[i] != MachineIoMode.NONE && neighbor._inputModes[i] != MachineIoMode.NONE)
    }

    protected fun canConnectTo(neighbor: TileEntity, side: EnumFacing): Boolean {
        return neighbor.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite)
    }

    open fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        clearInventory(itemBuffer, importItems)
        clearInventory(itemBuffer, exportItems)
    }

    open fun onPlacement() {}
    open fun onRemoval() {}

    fun getStackForm(amount: Int = 1): ItemStack {
        return ItemStack(ClayiumApi.BLOCK_MACHINE, amount, ClayiumApi.MTE_REGISTRY.getIdByKey(metaTileEntityId))
    }

    fun getNeighbor(side: EnumFacing): TileEntity? {
        return holder?.getNeighbor(side)
    }

    fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {}
    fun isInCreativeTab(tab: CreativeTabs): Boolean {
        return tab === CreativeTabs.SEARCH || tab === Clayium.creativeTab
    }

    companion object {
        protected fun clearInventory(itemBuffer: MutableList<ItemStack>, inventory: IItemHandlerModifiable) {
            for (i in 0..<inventory.slots) {
                val stack = inventory.getStackInSlot(i)
                if (!stack.isEmpty) {
                    itemBuffer.add(stack)
                    inventory.setStackInSlot(i, ItemStack.EMPTY)
                }
            }
        }
    }
}