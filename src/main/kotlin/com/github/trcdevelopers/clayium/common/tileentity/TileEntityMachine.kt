package com.github.trcdevelopers.clayium.common.tileentity

import com.github.trcdevelopers.clayium.common.blocks.IPipeConnectable
import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.util.CUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

abstract class TileEntityMachine : NeighborCacheTileEntityBase(), IPipeConnectable {
    var tier: Int = -1
        private set
    var frontFacing = EnumFacing.NORTH
        private set

    abstract var inputInventory: IItemHandlerModifiable
        protected set
    abstract var outputInventory: IItemHandlerModifiable
        protected set
    abstract var combinedInventory: IItemHandler
        protected set

    protected val _inputs = MutableList(6) { MachineIoMode.NONE }
    protected val _outputs = MutableList(6) { MachineIoMode.NONE }
    protected val _connections = BooleanArray(6)

    protected lateinit var validInputModes: List<MachineIoMode>
    protected lateinit var validOutputModes: List<MachineIoMode>

    val inputs get() = _inputs.toList()
    val outputs get() = _outputs.toList()
    val connections get() = _connections.copyOf()

    protected open fun initializeByTier(tier: Int) {}

    fun initValidIoModes(validInputModes: List<MachineIoMode>, validOutputModes: List<MachineIoMode>) {
        this.validInputModes = validInputModes
        this.validOutputModes = validOutputModes
    }

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        data.setInteger("tier", tier)
        data.setIntArray("validInputModes", IntArray(validInputModes.size) { validInputModes[it].id })
        data.setIntArray("validOutputModes", IntArray(validOutputModes.size) { validOutputModes[it].id})
        CUtils.writeItems(inputInventory, "inputInventory", data)
        CUtils.writeItems(outputInventory, "outputInventory", data)
        writeDynamic(data)
        return super.writeToNBT(data)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        initializeByTier(data.getInteger("tier"))
        validInputModes = data.getIntArray("validInputModes").map { MachineIoMode.byId(it) }
        validOutputModes = data.getIntArray("validOutputModes").map { MachineIoMode.byId(it) }
        CUtils.readItems(inputInventory, "inputInventory", data)
        CUtils.readItems(outputInventory, "outputInventory", data)
        readDynamic(data)
    }

    protected open fun writeDynamic(data: NBTTagCompound): NBTTagCompound {
        data.setIntArray("inputs", IntArray(6) { _inputs[it].id })
        data.setIntArray("outputs", IntArray(6) { _outputs[it].id })
        data.setByteArray("connections", ByteArray(6) { if (_connections[it]) 1 else 0 })
        data.setInteger("frontFacing", frontFacing.index)
        return data
    }
    protected open fun readDynamic(data: NBTTagCompound) {
        val tagInputs = data.getIntArray("inputs")
        val tagOutputs = data.getIntArray("outputs")
        val tagConnections = data.getByteArray("connections")
        for (i in 0..<6) {
            _inputs[i] = MachineIoMode.byId(tagInputs[i])
            _outputs[i] = MachineIoMode.byId(tagOutputs[i])
            _connections[i] = tagConnections[i] == 1.toByte()
        }
        frontFacing = EnumFacing.byIndex(data.getInteger("frontFacing"))
    }

    override fun getUpdateTag(): NBTTagCompound {
        return writeToNBT(NBTTagCompound())
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(pos, blockMetadata, writeDynamic(NBTTagCompound()))
    }

    override fun handleUpdateTag(tag: NBTTagCompound) {
        readDynamic(tag)
    }

    override fun onNeighborChanged(facing: EnumFacing) {
        super.onNeighborChanged(facing)
        when (val neighbor = getNeighbor(facing)) {
            is TileEntityMachine -> {

            }
            else -> {
                _connections[facing.index] = neighbor?.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.opposite) == true
            }
        }
    }

    override fun acceptInputFrom(side: EnumFacing) = _inputs[side.index] != MachineIoMode.NONE
    override fun acceptOutputTo(side: EnumFacing) = _outputs[side.index] != MachineIoMode.NONE

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }

    fun getInput(side: EnumFacing): MachineIoMode {
        return _inputs[side.index]
    }

    fun getOutput(side: EnumFacing): MachineIoMode {
        return _outputs[side.index]
    }
}