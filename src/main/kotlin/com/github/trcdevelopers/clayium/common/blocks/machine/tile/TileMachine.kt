package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.IPipeConnectable
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasByteArray
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasInt
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasIntArray
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
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

    protected val _inputs = MutableList(6) { MachineIoMode.NONE }
    protected val _outputs = MutableList(6) { MachineIoMode.NONE }
    protected val _connections = BooleanArray(6)

    protected lateinit var validInputModes: List<MachineIoMode>
    protected lateinit var validOutputModes: List<MachineIoMode>

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

    open fun onBlockPlaced(player: EntityLivingBase, stack: ItemStack) {
        this.currentFacing = player.horizontalFacing.opposite
    }

    override fun update() {
        if (!world.isRemote) {
            autoIoHandler.doWork()
        }
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
            if (compound.hasInt("tier")) compound.getInteger("tier") else 0,
            if (compound.hasIntArray("validInputs")) compound.getIntArray("validInputs").map { MachineIoMode.byId(it) } else listOf(MachineIoMode.NONE),
            if (compound.hasIntArray("validOutputs")) compound.getIntArray("validOutputs").map { MachineIoMode.byId(it) } else listOf(MachineIoMode.NONE),
        )
        currentFacing = if (compound.hasInt("facing")) EnumFacing.byIndex(compound.getInteger("facing")) else EnumFacing.NORTH
        readDynamicData(compound)
        super.readFromNBT(compound)
    }

    override fun getUpdateTag(): NBTTagCompound {
        return writeToNBT(NBTTagCompound())
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(
            pos, blockMetadata,
            writeDynamicData(NBTTagCompound())
        )
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        readDynamicData(pkt.nbtCompound)
        if (world.isRemote) {
            Clayium.LOGGER.info("onDataPacket: facing: $currentFacing")
            world.markBlockRangeForRenderUpdate(pos, pos)
        }
    }

    private fun writeDynamicData(compound: NBTTagCompound): NBTTagCompound {
        compound.setIntArray("inputs", IntArray(6) { _inputs[it].id })
        compound.setIntArray("outputs", IntArray(6) { _outputs[it].id })
        compound.setByteArray("connections", ByteArray(6) { if (_connections[it]) 1 else 0 })
        compound.setInteger("facing", currentFacing.index)
        return compound
    }

    /**
     * reads input, output, connections from NBT
     */
    private fun readDynamicData(compound: NBTTagCompound) {
        val tagInputs = if (compound.hasIntArray("inputs")) compound.getIntArray("inputs") else IntArray(6) { 0 }
        val tagOutputs = if (compound.hasIntArray("outputs")) compound.getIntArray("outputs") else IntArray(6) { 0 }
        val tagConnections = if (compound.hasByteArray("connections")) compound.getByteArray("connections") else ByteArray(6) { 0 }
        for (side in EnumFacing.entries) {
            val i = side.index
            _inputs[i] = MachineIoMode.byId(tagInputs[i])
            _outputs[i] = MachineIoMode.byId(tagOutputs[i])
            _connections[i] = tagConnections[i] == 1.toByte()
        }
        currentFacing = if (compound.hasInt("facing")) EnumFacing.byIndex(compound.getInteger("facing")) else EnumFacing.NORTH
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

    override fun onRightClicked(toolType: ItemClayConfigTool.ToolType, worldIn: World, posIn: BlockPos, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (worldIn.isRemote) return
        val oldState = worldIn.getBlockState(posIn)
        when (toolType) {
            ItemClayConfigTool.ToolType.PIPING -> { /* handled by Block */ }
            ItemClayConfigTool.ToolType.INSERTION -> toggleInput(clickedSide)
            ItemClayConfigTool.ToolType.EXTRACTION -> toggleOutput(clickedSide)
            ItemClayConfigTool.ToolType.ROTATION -> {
                if (clickedSide.axis.isVertical) return
                if (currentFacing == clickedSide) {
                    currentFacing = currentFacing.opposite
                    val oldInputs = _inputs.toList()
                    val oldOutputs = _outputs.toList()
                    for (side in EnumFacing.HORIZONTALS) {
                        val rotatedSide = side.opposite
                        _inputs[rotatedSide.index] = oldInputs[side.index]
                        _outputs[rotatedSide.index] = oldOutputs[side.index]
                    }
                } else {
                    while (currentFacing != clickedSide) {
                        val oldInputs = _inputs.toList()
                        val oldOutputs = _outputs.toList()
                        currentFacing = currentFacing.rotateY()
                        for (side in EnumFacing.HORIZONTALS) {
                            val rotatedSide = side.rotateY()
                            _inputs[rotatedSide.index] = oldInputs[side.index]
                            _outputs[rotatedSide.index] = oldOutputs[side.index]
                            Clayium.LOGGER.info("inputs: ${_inputs}")
                        }
                    }
                }
            }
            ItemClayConfigTool.ToolType.FILTER_REMOVER -> TODO()
        }
        worldIn.notifyBlockUpdate(posIn, oldState, oldState, Constants.BlockFlags.DEFAULT)
    }

    fun onNeighborChange(side: EnumFacing) {
        this.refreshConnection(side)
    }

    protected fun toggleInput(side: EnumFacing) {
        val current = _inputs[side.index]
        val next = validInputModes[(validInputModes.indexOf(current) + 1) % validInputModes.size]
        _inputs[side.index] = next
        this.refreshConnection(side)
        this.markDirty()
    }

    protected fun toggleOutput(side: EnumFacing) {
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