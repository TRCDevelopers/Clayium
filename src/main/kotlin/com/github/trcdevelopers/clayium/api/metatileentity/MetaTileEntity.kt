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
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.capability.impl.RangedItemHandlerProxy
import com.github.trcdevelopers.clayium.api.gui.MetaTileEntityGuiFactory
import com.github.trcdevelopers.clayium.api.metatileentity.interfaces.ISyncedTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.IPipeConnectable
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.*
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.*
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import org.jetbrains.annotations.MustBeInvokedByOverriders

abstract class MetaTileEntity(
    val metaTileEntityId: ResourceLocation,
    val tier: ITier,
    open val validInputModes: List<MachineIoMode>,
    open val validOutputModes: List<MachineIoMode>,
    /**
     * used in item/block name and gui title
     */
    val translationKey: String,
) : ISyncedTileEntity, IGuiHolder<PosGuiData>, IPipeConnectable {

    val forgeRarity = tier.rarity

    var holder: MetaTileEntityHolder? = null
    val world: World? get() = holder?.world
    val pos: BlockPos? get() = holder?.pos

    open val faceTexture: ResourceLocation? = null
    open val allFaceTextures get() = listOf(faceTexture)

    protected val mteTraits = mutableMapOf<String, MTETrait>()
    private val traitByNetworkId = Int2ObjectOpenHashMap<MTETrait>()

    abstract val importItems: IItemHandlerModifiable
    abstract val exportItems: IItemHandlerModifiable
    abstract val itemInventory: IItemHandler
    abstract val autoIoHandler: AutoIoHandler
    var hasNotifiedInputs = false
    var hasNotifiedOutputs = false

    abstract fun createMetaTileEntity(): MetaTileEntity

    protected val _inputModes = MutableList(6) { NONE }
    protected val _outputModes = MutableList(6) { NONE }
    private val _connectionsCache = BooleanArray(6)
    val inputModes get() = _inputModes.toList()
    val outputModes get() = _outputModes.toList()
    val connectionsCache get() = _connectionsCache.copyOf()

    open val hasFrontFacing = true
    var frontFacing = EnumFacing.NORTH
        protected set

    private var timer = 0L
    private val timerOffset = (0..19).random()
    val offsetTimer: Long get() = timer + timerOffset

    /**
     * If true, [faceTexture] will be added to all [EnumFacing].
     */
    open val useFaceForAllSides = false

    @SideOnly(Side.CLIENT)
    abstract fun registerItemModel(item: Item, meta: Int)

    fun addMetaTileEntityTrait(trait: MTETrait) {
        mteTraits[trait.name] = trait
        traitByNetworkId[trait.networkId] = trait
    }

    fun markDirty() = holder?.markDirty()

    open fun update() {
        mteTraits.values.forEach(MTETrait::update)
        timer++
    }

    open fun writeToNBT(data: NBTTagCompound) {
        data.setByte("frontFacing", frontFacing.index.toByte())
        data.setByteArray("inputModes", ByteArray(6) { _inputModes[it].id.toByte() })
        data.setByteArray("outputModes", ByteArray(6) { _outputModes[it].id.toByte() })
        data.setByteArray("connections", ByteArray(6) { if (_connectionsCache[it]) 1 else 0 })
        CUtils.writeItems(importItems, "importInventory", data)
        CUtils.writeItems(exportItems, "exportInventory", data)
        for ((name, trait) in mteTraits) {
            data.setTag(name, trait.serializeNBT())
        }
    }

    open fun readFromNBT(data: NBTTagCompound) {
        frontFacing = EnumFacing.byIndex(data.getByte("frontFacing").toInt())
        data.getByteArray("inputModes").forEachIndexed { i, id -> _inputModes[i] = MachineIoMode.entries[id.toInt()] }
        data.getByteArray("outputModes").forEachIndexed { i, id -> _outputModes[i] = MachineIoMode.entries[id.toInt()] }
        data.getByteArray("connections").forEachIndexed { i, b -> _connectionsCache[i] = (b == 1.toByte()) }
        CUtils.readItems(importItems, "importInventory", data)
        CUtils.readItems(exportItems, "exportInventory", data)
        for ((name, trait) in mteTraits) {
            trait.deserializeNBT(data.getCompoundTag(name))
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        buf.writeByte(frontFacing.index)
        for (i in 0..5) {
            buf.writeByte(_inputModes[i].id)
            buf.writeByte(_outputModes[i].id)
            buf.writeBoolean(_connectionsCache[i])
        }
        buf.writeVarInt(traitByNetworkId.size)
        for ((id, trait) in traitByNetworkId) {
            buf.writeVarInt(id)
            trait.writeInitialSyncData(buf)
        }
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        frontFacing = EnumFacing.byIndex(buf.readByte().toInt())
        for (i in 0..5) {
            _inputModes[i] = MachineIoMode.entries[buf.readByte().toInt()]
            _outputModes[i] = MachineIoMode.entries[buf.readByte().toInt()]
            _connectionsCache[i] = buf.readBoolean()
        }
        val numberOfTraits = buf.readVarInt()
        for (i in 0..<numberOfTraits) {
            val id = buf.readVarInt()
            traitByNetworkId[id]?.receiveInitialSyncData(buf)
                ?: Clayium.LOGGER.error("Could not find MTETrait with id $id at $pos")
        }
    }

    override fun writeCustomData(discriminator: Int, dataWriter: PacketBuffer.() -> Unit) {
        this.holder?.writeCustomData(discriminator, dataWriter)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_FRONT_FACING -> {
                frontFacing = EnumFacing.byIndex(buf.readByte().toInt())
                this.scheduleRenderUpdate()
            }
            UPDATE_INPUT_MODE -> {
                _inputModes[buf.readByte().toInt()] = MachineIoMode.entries[buf.readByte().toInt()]
                this.scheduleRenderUpdate()
            }
            UPDATE_OUTPUT_MODE -> {
                _outputModes[buf.readByte().toInt()] = MachineIoMode.entries[buf.readByte().toInt()]
                this.scheduleRenderUpdate()
            }
            UPDATE_CONNECTIONS -> {
                _connectionsCache[buf.readByte().toInt()] = buf.readBoolean()
                this.scheduleRenderUpdate()
            }
            SYNC_MTE_TRAIT -> {
                val traitNetworkId = buf.readVarInt()
                val trait = traitByNetworkId[traitNetworkId]
                    ?: run {
                        Clayium.LOGGER.error("Could not find MTETrait with id $traitNetworkId at $pos")
                        return
                    }
                trait.receiveCustomData(buf.readVarInt(), buf)
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

    open fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemInventory)
            val inputSlots = when (inputModes[facing.index]) {
                FIRST -> RangedItemHandlerProxy(importItems, availableSlot = 0)
                SECOND -> RangedItemHandlerProxy(importItems, availableSlot = 1)
                ALL -> importItems
                else -> null
            }
            val outputSlots = when (outputModes[facing.index]) {
                FIRST -> RangedItemHandlerProxy(exportItems, availableSlot = 0)
                SECOND -> RangedItemHandlerProxy(exportItems, availableSlot = 1)
                ALL -> exportItems
                else -> null
            }
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(ItemHandlerProxy(inputSlots, outputSlots))
        }
        return null
    }

    open fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        val pos = this.pos ?: return
        if (this.canOpenGui()) {
            MetaTileEntityGuiFactory.open(player, pos)
        }
    }

    open fun canOpenGui() = true

    fun onToolClick(toolType: ItemClayConfigTool.ToolType, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (this.world?.isRemote == true) return
        val world = this.world ?: return
        val pos = this.pos ?: return
        when (toolType) {
            PIPING -> {
                world.setBlockState(pos, world.getBlockState(pos).cycleProperty(IS_PIPE))
            }
            INSERTION -> {
                this.toggleInput(clickedSide)
            }
            EXTRACTION -> {
                this.toggleOutput(clickedSide)
            }
            ROTATION -> {
                this.rotate(clickedSide)
                EnumFacing.entries.forEach(this::refreshConnection)
            }
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
        this.refreshConnection(side)
        (this.getNeighbor(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
        writeCustomData(UPDATE_INPUT_MODE) {
            writeByte(side.index)
            writeByte(_inputModes[side.index].id)
        }
    }

    protected fun toggleOutput(side: EnumFacing) {
        val current = _outputModes[side.index]
        _outputModes[side.index] = validOutputModes[(validOutputModes.indexOf(current) + 1) % validOutputModes.size]
        this.refreshConnection(side)
        (this.getNeighbor(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
        writeCustomData(UPDATE_OUTPUT_MODE) {
            writeByte(side.index)
            writeByte(_outputModes[side.index].id)
        }
    }

    protected fun refreshConnection(side: EnumFacing) {
        val neighborTileEntity = this.getNeighbor(side) ?: return
        val neighborMetaTileEntity = (neighborTileEntity as? MetaTileEntityHolder)?.metaTileEntity
        val i = side.index
        if (neighborMetaTileEntity == null) {
            _connectionsCache[i] = this.canConnectTo(neighborTileEntity, side)
        } else {
            _connectionsCache[i] = (this.canConnectToMte(neighborMetaTileEntity, side) || neighborMetaTileEntity.canConnectToMte(this, side.opposite))
        }
        writeCustomData(UPDATE_CONNECTIONS) {
            writeByte(i)
            writeBoolean(_connectionsCache[i])
        }
    }

    protected fun refreshNeighborConnection(side: EnumFacing) {
        (this.getNeighbor(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
    }

    protected open fun canConnectToMte(neighbor: MetaTileEntity, side: EnumFacing): Boolean {
        val i = side.index
        val o = side.opposite.index
        return (this._inputModes[i] != NONE && neighbor._outputModes[o] != NONE
                || this._outputModes[i] != NONE && neighbor._inputModes[o] != NONE)
    }

    protected fun canConnectTo(neighbor: TileEntity, side: EnumFacing): Boolean {
        return neighbor.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite)
    }

    open fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        clearInventory(itemBuffer, importItems)
        clearInventory(itemBuffer, exportItems)
    }

    /**
     * called on both client and server.
     */
    @MustBeInvokedByOverriders
    open fun changeIoModesOnPlacement(placer: EntityLivingBase) {
        if (this.hasFrontFacing) {
            this.frontFacing = placer.horizontalFacing.opposite
        }
        EnumFacing.entries.forEach(this::refreshConnection)
    }

    @MustBeInvokedByOverriders
    open fun onPlacement() {}

    open fun onRemoval() {}

    fun getStackForm(amount: Int = 1): ItemStack {
        return ItemStack(ClayiumApi.BLOCK_MACHINE, amount, ClayiumApi.MTE_REGISTRY.getIdByKey(metaTileEntityId))
    }

    open fun onNeighborChanged(facing: EnumFacing) {
        this.refreshConnection(facing)
    }
    open fun neighborChanged() {}

    fun getNeighbor(side: EnumFacing) = holder?.getNeighbor(side)
    fun scheduleRenderUpdate() = holder?.scheduleRenderUpdate()

    @SideOnly(Side.CLIENT)
    @MustBeInvokedByOverriders
    open fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18n.format("tooltip.clayium.tier", tier.numeric))
    }

    open fun isInCreativeTab(tab: CreativeTabs): Boolean {
        return tab === CreativeTabs.SEARCH || tab === Clayium.creativeTab
    }

    open fun shouldRenderInPass(pass: Int) = (pass == 0)
    open fun getMaxRenderDistanceSquared(): Double = 4096.0
    /**
     * null for use TileEntity defaults.
     */
    open val renderBoundingBox: AxisAlignedBB? = null

    companion object {

        val onlyNoneList = listOf(NONE)

        val validInputModesLists = listOf(
            listOf(NONE, CE),
            listOf(ALL, CE, NONE),
            listOf(ALL, FIRST, SECOND, CE, NONE)
        )

        val validOutputModesLists = listOf(
            onlyNoneList,
            listOf(ALL, NONE),
            listOf(ALL, FIRST, SECOND, NONE)
        )

        fun clearInventory(itemBuffer: MutableList<ItemStack>, inventory: IItemHandlerModifiable) {
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