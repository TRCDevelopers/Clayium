package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.IPipeConnectable
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

/**
 * root of all tile machines
 *
 * - has auto IO handler
 */
abstract class TileMachine : TileEntity(), ITickable, IPipeConnectable, ItemClayConfigTool.Listener {

    var tier: Int = -1
        private set

    var currentFacing: EnumFacing = EnumFacing.NORTH
        private set

    private val _inputs = MutableList(6) { MachineIoMode.NONE }
    private val _outputs = MutableList(6) { MachineIoMode.NONE }
    private val _connections = BooleanArray(6)

    private lateinit var validInputModes: List<MachineIoMode>
    private lateinit var validOutputModes: List<MachineIoMode>

    protected abstract var autoIoHandler: AutoIoHandler

    val inputs get() = _inputs.toList()
    val outputs get() = _outputs.toList()
    val connections get() = _connections.copyOf()

    abstract fun openGui(player: EntityPlayer, world: World, pos: BlockPos)
    abstract fun getItemHandler(): IItemHandler

    protected open fun initParams(tier: Int, inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        this.tier = tier

        this.validInputModes = inputModes
        this.validOutputModes = outputModes
    }

    override fun update() {
        autoIoHandler.doWork()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ITEM_HANDLER_CAPABILITY) ITEM_HANDLER_CAPABILITY.cast(getItemHandler()) else super.getCapability(capability, facing)

    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("tier", tier)
        compound.setInteger("facing", currentFacing.index)
        compound.setIntArray("validInputs", validInputModes.map { it.id }.toIntArray())
        compound.setIntArray("validOutputs", validOutputModes.map { it.id }.toIntArray())
        compound.setIntArray("inputs", _inputs.map { it.id }.toIntArray())
        compound.setIntArray("outputs", _outputs.map { it.id }.toIntArray())
        compound.setByteArray("connections", ByteArray(6) { if (_connections[it]) 1 else 0 })
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        initParams(
            compound.getInteger("tier"),
            compound.getIntArray("validInputs").map { MachineIoMode.byId(it) },
            compound.getIntArray("validOutputs").map { MachineIoMode.byId(it) }
        )
        currentFacing = EnumFacing.byIndex(compound.getInteger("facing"))
        val readInputs = compound.getIntArray("inputs")
        val readOutputs = compound.getIntArray("outputs")
        val readConnections = compound.getByteArray("connections")
        for (side in EnumFacing.entries) {
            val i = side.index
            _inputs[i] = MachineIoMode.byId(readInputs[i])
            _outputs[i] = MachineIoMode.byId(readOutputs[i])
            _connections[i] = readConnections[i] == 1.toByte()
        }
        super.readFromNBT(compound)
    }

    override fun getUpdateTag(): NBTTagCompound {
        return writeToNBT(NBTTagCompound())
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(
            pos, blockMetadata,
            NBTTagCompound().apply {
                setIntArray("inputs", IntArray(6) { _inputs[it].id })
                setIntArray("outputs", IntArray(6) { _outputs[it].id })
                setByteArray("connections",  ByteArray(6) { if (_connections[it]) 1 else 0 })
            }
        )
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        pkt.nbtCompound.let {
            val tagInputs = it.getIntArray("inputs")
            val tagOutputs = it.getIntArray("outputs")
            val tagConnections = it.getByteArray("connections")
            for (side in EnumFacing.entries) {
                val i = side.index
                _inputs[i] = MachineIoMode.byId(tagInputs[i])
                _outputs[i] = MachineIoMode.byId(tagOutputs[i])
                _connections[i] = tagConnections[i] == 1.toByte()
            }
        }
        if (world.isRemote) {
            world.markBlockRangeForRenderUpdate(pos, pos)
        }
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }

    override fun isImporting(side: EnumFacing): Boolean {
        return _inputs[side.index] != MachineIoMode.NONE
    }

    override fun isExporting(side: EnumFacing): Boolean {
        return _outputs[side.index] != MachineIoMode.NONE
    }

    override fun onRightClicked(toolType: ItemClayConfigTool.ToolType, world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (world.isRemote) return
        when (toolType) {
            ItemClayConfigTool.ToolType.PIPING -> { /* handled by Block */ }
            ItemClayConfigTool.ToolType.INSERTION -> toggleInput(facing)
            ItemClayConfigTool.ToolType.EXTRACTION -> toggleOutput(facing)
            ItemClayConfigTool.ToolType.ROTATION -> {
                // facing is handled by Block
                if (facing.axis.isVertical) return
                val from = this.currentFacing.horizontalIndex
                val to = facing.horizontalIndex
                val offset = from - to
            }
            ItemClayConfigTool.ToolType.FILTER_REMOVER -> TODO()
        }
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.DEFAULT)
    }

    fun onNeighborChange(side: EnumFacing) {
        this.refreshConnection(side)
    }

    private fun toggleInput(side: EnumFacing) {
        val current = _inputs[side.index]
        val next = validInputModes[(validInputModes.indexOf(current) + 1) % validInputModes.size]
        _inputs[side.index] = next
        this.refreshConnection(side)
        this.markDirty()
    }

    private fun toggleOutput(side: EnumFacing) {
        val current = _outputs[side.index]
        val next = validOutputModes[(validOutputModes.indexOf(current) + 1) % validOutputModes.size]
        _outputs[side.index] = next
        this.refreshConnection(side)
        this.markDirty()
    }

    private fun refreshConnection(side: EnumFacing) {
        val i = side.index
        val o = side.opposite.index
        when (val neighborTile = world.getTileEntity(pos.offset(side))) {
            is TileMachine -> {
                Clayium.LOGGER.info("neighborTile: $neighborTile")
                this._connections[i] = (isImporting(side) && neighborTile.isExporting(side.opposite)) || (isExporting(side) && neighborTile.isImporting(side.opposite))
            }
            else -> {
                this._connections[i] = neighborTile?.hasCapability(ITEM_HANDLER_CAPABILITY, side.opposite) == true
            }
        }
    }

    protected inner class AutoIoHandler(
        private val intervalTick: Int,
        private val amountPerAction: Int,
    ) {

        private var ticked = 0

        fun doWork() {
            if (world.isRemote) return
            if (ticked < intervalTick) {
                ticked++
                return
            }

            var remainingImportWork = this.amountPerAction
            var remainingExportWork = this.amountPerAction
            for (side in EnumFacing.entries) {
                if (remainingImportWork > 0 && isImporting(side)) {
                    remainingImportWork -= this.transferItemStack(
                        from = world.getTileEntity(pos.offset(side))?.getCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                        to = getItemHandler(),
                        amount = remainingImportWork,
                    )
                }
                if (remainingExportWork > 0 && isExporting(side)) {
                    remainingExportWork -= this.transferItemStack(
                        from = getItemHandler(),
                        to = world.getTileEntity(pos.offset(side))?.getCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                        amount = remainingExportWork,
                    )
                }
            }
        }

        private fun transferItemStack(
            from: IItemHandler,
            to: IItemHandler,
            amount: Int,
        ) : Int {
            var remainingWork = amount

            for (i in 0..<from.slots) {
                // get how many items can be inserted
                val extractedStack = from.extractItem(i, remainingWork, true)
                if (extractedStack.isEmpty) continue
                val insertedItemCount = extractedStack.count - insertToInventory(to, extractedStack, true).count
                if (insertedItemCount == 0) continue

                // actually insert the items
                insertToInventory(to, from.extractItem(i, insertedItemCount, false), false)
                remainingWork -= insertedItemCount
            }
            return remainingWork
        }

        /**
         * @param handler The target inventory of the insertion
         * @param stack The stack to insert. This stack will not be modified.
         * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack)
         */
        private fun insertToInventory(handler: IItemHandler, stack: ItemStack, simulate: Boolean): ItemStack {
            var remaining = stack.copy()
            for (i in 0..<handler.slots) {
                remaining = handler.insertItem(i, remaining, simulate)
                if (remaining.isEmpty) break
            }
            return remaining
        }
    }

    companion object {
        @JvmStatic
        val ITEM_HANDLER_CAPABILITY: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}