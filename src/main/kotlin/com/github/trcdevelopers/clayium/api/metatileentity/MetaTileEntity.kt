package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.factory.PosGuiData
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_CONNECTIONS
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_FRONT_FACING
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_INPUT_MODE
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_OUTPUT_MODE
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.metatileentity.interfaces.ISyncedTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine.Companion.IS_PIPE
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.*
import com.github.trcdevelopers.clayium.common.tileentity.AutoIoHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

abstract class MetaTileEntity(
    val metaTileEntityId: ResourceLocation,
    val tier: Int,
    val validInputModes: List<MachineIoMode>,
    val validOutputModes: List<MachineIoMode>,
) : ISyncedTileEntity, IGuiHolder<PosGuiData> {
    var holder: MetaTileEntityHolder? = null
    val world: World? get() = holder?.world
    val pos: BlockPos? get() = holder?.pos

    abstract val importItems: IItemHandlerModifiable
    abstract val exportItems: IItemHandlerModifiable
    val itemInventory = ItemHandlerProxy(inputInventory = importItems, outputInventory = exportItems)
    abstract val autoIoHandler: AutoIoHandler

    abstract fun createMetaTileEntity(): MetaTileEntity

    private val _inputModes = MutableList(6) { MachineIoMode.NONE }
    private val _outputModes = MutableList(6) { MachineIoMode.NONE }
    private val _connectionsCache = BooleanArray(6)
    val inputModes get() = _inputModes.toList()
    val outputModes get() = _outputModes.toList()
    val connectionsCache get() = _connectionsCache.copyOf()

    var frontFacing = EnumFacing.NORTH
        protected set

    fun markDirty() = holder?.markDirty()

    open fun writeToNBT(data: NBTTagCompound) {
        data.setByte("frontFacing", frontFacing.index.toByte())
        data.setByteArray("inputModes", ByteArray(6) { _inputModes[it].id.toByte() })
        data.setByteArray("outputModes", ByteArray(6) { _outputModes[it].id.toByte() })
    }

    open fun readFromNBT(data: NBTTagCompound) {
        frontFacing = EnumFacing.byIndex(data.getByte("frontFacing").toInt())
        data.getByteArray("inputModes").forEachIndexed { i, id -> _inputModes[i] = MachineIoMode.entries[id.toInt()] }
        data.getByteArray("outputModes").forEachIndexed { i, id -> _outputModes[i] = MachineIoMode.entries[id.toInt()] }
        EnumFacing.entries.forEach(this::refreshConnection)
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

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_FRONT_FACING -> frontFacing = EnumFacing.byIndex(buf.readByte().toInt())
            UPDATE_INPUT_MODE -> _inputModes[buf.readByte().toInt()] = MachineIoMode.entries[buf.readByte().toInt()]
            UPDATE_OUTPUT_MODE -> _outputModes[buf.readByte().toInt()] = MachineIoMode.entries[buf.readByte().toInt()]
            UPDATE_CONNECTIONS -> _connectionsCache[buf.readByte().toInt()] = buf.readBoolean()
        }
    }
}