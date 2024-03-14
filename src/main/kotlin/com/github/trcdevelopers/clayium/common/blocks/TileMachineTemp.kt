package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
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
abstract class TileMachineTemp : TileEntity(), ITickable, IPipeConnectable, ItemClayConfigTool.Listener {

    var tier: Int = -1
        private set

    var currentFacing: EnumFacing = EnumFacing.NORTH
        private set

    private val _inputs = MutableList(6) { MachineIoMode.NONE }
    private val _outputs = MutableList(6) { MachineIoMode.NONE }

    private lateinit var validInputModes: List<MachineIoMode>
    private lateinit var validOutputModes: List<MachineIoMode>

    private lateinit var autoIoHandler: AutoIoHandler

    protected abstract val itemStackHandler: IItemHandler

    val inputs get() = _inputs.toList()
    val outputs get() = _outputs.toList()

    private fun initParams(tier: Int) {
        this.tier = tier
        //todo: proper values
        autoIoHandler = AutoIoHandler(20, 64)
    }

    private fun setValidIoModes(inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
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
        return if (capability === ITEM_HANDLER_CAPABILITY) ITEM_HANDLER_CAPABILITY.cast(itemStackHandler) else super.getCapability(capability, facing)

    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("tier", tier)
        compound.setInteger("facing", currentFacing.index)
        compound.setIntArray("validInputs", validInputModes.map { it.id }.toIntArray())
        compound.setIntArray("validOutputs", validOutputModes.map { it.id }.toIntArray())
        compound.setIntArray("inputs", _inputs.map { it.id }.toIntArray())
        compound.setIntArray("outputs", _outputs.map { it.id }.toIntArray())
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        initParams(compound.getInteger("tier"))
        currentFacing = EnumFacing.byIndex(compound.getInteger("facing"))
        setValidIoModes(
            compound.getIntArray("validInputs").map { MachineIoMode.byId(it) },
            compound.getIntArray("validOutputs").map { MachineIoMode.byId(it) }
        )
        for (side in EnumFacing.entries) {
            val i = side.index
            _inputs[i] = MachineIoMode.byId(compound.getIntArray("inputs")[i])
            _outputs[i] = MachineIoMode.byId(compound.getIntArray("outputs")[i])
        }
        super.readFromNBT(compound)
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(
            pos, blockMetadata,
            NBTTagCompound().apply {
                setIntArray("inputs", _inputs.map { it.id }.toIntArray())
                setIntArray("outputs", _outputs.map { it.id }.toIntArray())
            }
        )
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        pkt.nbtCompound.let {
            val tagInputs = it.getIntArray("inputs")
            val tagOutputs = it.getIntArray("outputs")
            for (side in EnumFacing.entries) {
                val i = side.index
                _inputs[i] = MachineIoMode.byId(tagInputs[i])
                _outputs[i] = MachineIoMode.byId(tagOutputs[i])
            }
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

    override fun onRightClicked(toolType: ItemClayConfigTool.ToolType, worldIn: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (worldIn.isRemote) return
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
                TODO()
            }
            ItemClayConfigTool.ToolType.FILTER_REMOVER -> TODO()
        }
        worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), Constants.BlockFlags.DEFAULT)
    }

    private fun toggleInput(side: EnumFacing) {
        val current = _inputs[side.index]
        val next = validInputModes[(validInputModes.indexOf(current) + 1) % validInputModes.size]
        _inputs[side.index] = next
        this.markDirty()
    }

    private fun toggleOutput(side: EnumFacing) {
        val current = _outputs[side.index]
        val next = validOutputModes[(validOutputModes.indexOf(current) + 1) % validOutputModes.size]
        _outputs[side.index] = next
        this.markDirty()
    }

    private inner class AutoIoHandler(
        private val intervalTick: Int,
        private val amountPerAction: Int,
    ) {
        fun doWork() {

        }
    }

    companion object {
        @JvmStatic
        val ITEM_HANDLER_CAPABILITY: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}