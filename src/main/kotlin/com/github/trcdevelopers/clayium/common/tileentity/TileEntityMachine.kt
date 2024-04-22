package com.github.trcdevelopers.clayium.common.tileentity

import com.github.trcdevelopers.clayium.common.blocks.IPipeConnectable
import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.*
import com.github.trcdevelopers.clayium.common.util.CUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

abstract class TileEntityMachine : NeighborCacheTileEntityBase(), IPipeConnectable, ITickable, ItemClayConfigTool.Listener {
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
    open val autoIoHandler: AutoIoHandler by lazy {
        require(tier != -1) { "Tier is not initialized" }
        AutoIoHandler(this)
    }

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
        initializeByTier(data.getInteger("tier"))
        validInputModes = data.getIntArray("validInputModes").map { MachineIoMode.byId(it) }
        validOutputModes = data.getIntArray("validOutputModes").map { MachineIoMode.byId(it) }
        CUtils.readItems(inputInventory, "inputInventory", data)
        CUtils.readItems(outputInventory, "outputInventory", data)
        readDynamic(data)
        super.readFromNBT(data)
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

    override fun update() {
        autoIoHandler.tick()
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
        val i = facing.index
        when (val neighbor = getNeighbor(facing)) {
            is TileEntityMachine -> {
                _connections[i] = (this.canImportFrom(facing) && neighbor.canExportTo(facing.opposite)) ||
                        (this.canExportTo(facing) && neighbor.canImportFrom(facing.opposite))
            }
            else -> {
                _connections[i] = neighbor?.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.opposite) == true
            }
        }
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }

    // IPipeConnectable
    override fun getInput(side: EnumFacing): MachineIoMode {
        return _inputs[side.index]
    }

    override fun getOutput(side: EnumFacing): MachineIoMode {
        return _outputs[side.index]
    }

    // ClayTool Listener
    override fun onRightClicked(
        toolType: ItemClayConfigTool.ToolType, worldIn: World, posIn: BlockPos, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float
    ) {
        when (toolType) {
            PIPING -> worldIn.setBlockState(posIn, worldIn.getBlockState(posIn).cycleProperty(BlockMachine.IS_PIPE))
            INSERTION -> toggleInput(clickedSide)
            EXTRACTION -> toggleOutput(clickedSide)
            ROTATION -> rotate(clickedSide)
            FILTER_REMOVER -> TODO()
        }
    }

    private fun toggleInput(side: EnumFacing) {
        _inputs[side.index] = validInputModes[(_inputs[side.index].id + 1) % validInputModes.size]
        this.markDirty()
    }

    private fun toggleOutput(side: EnumFacing) {
        _outputs[side.index] = validOutputModes[(_outputs[side.index].id + 1) % validOutputModes.size]
        this.markDirty()
    }

    private fun rotate(side: EnumFacing) {
        if (side.axis.isVertical) return
        if (frontFacing == side) {
            frontFacing = frontFacing.opposite
            val oldInputs = _inputs.toList()
            val oldOutputs = _outputs.toList()
            for (side in EnumFacing.HORIZONTALS) {
                val rotatedSide = side.opposite
                _inputs[rotatedSide.index] = oldInputs[side.index]
                _outputs[rotatedSide.index] = oldOutputs[side.index]
            }
        } else {
            while (frontFacing != side) {
                val oldInputs = _inputs.toList()
                val oldOutputs = _outputs.toList()
                frontFacing = frontFacing.rotateY()
                for (side in EnumFacing.HORIZONTALS) {
                    val rotatedSide = side.rotateY()
                    _inputs[rotatedSide.index] = oldInputs[side.index]
                    _outputs[rotatedSide.index] = oldOutputs[side.index]
                }
            }
        }
    }
}