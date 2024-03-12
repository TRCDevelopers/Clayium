package com.github.trcdevelopers.clayium.common.blocks.machine

import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants

class TileEntityMachine : TileEntity(), ItemClayConfigTool.Listener {

    var tier = -1
        private set
    var isPipe = false
        private set

    private val _inputs = MutableList(6) { MachineIoMode.NONE }
    val inputs get() = _inputs.toList()
    private val _outputs = MutableList(6) { MachineIoMode.NONE }
    val outputs get() = _outputs.toList()

    private lateinit var validInputModes: List<MachineIoMode>
    private lateinit var validOutputModes: List<MachineIoMode>

    private fun initParams(tier: Int) {

    }

    private fun setValidIoModes(inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        this.validInputModes = inputModes
        this.validOutputModes = outputModes
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        tier = compound.getInteger("tier")

        val tagInputs = compound.getIntArray("inputs")
        _inputs.clear()
        _inputs.addAll(tagInputs.map { MachineIoMode.byId(it) })
        val tagOutputs = compound.getIntArray("outputs")
        _outputs.clear()
        _outputs.addAll(tagOutputs.map { MachineIoMode.byId(it) })

        validInputModes = compound.getIntArray("validInputs").map { MachineIoMode.byId(it) }
        validOutputModes = compound.getIntArray("validOutputs").map { MachineIoMode.byId(it) }

        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("tier", tier)
        compound.setIntArray("inputs", _inputs.map { it.id }.toIntArray())
        compound.setIntArray("outputs", _outputs.map { it.id }.toIntArray())
        compound.setIntArray("validInputs", validInputModes.map { it.id }.toIntArray())
        compound.setIntArray("validOutputs", validOutputModes.map { it.id }.toIntArray())
        return super.writeToNBT(compound)
    }

    override fun getUpdateTag(): NBTTagCompound {
        return writeToNBT(NBTTagCompound())
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
        pkt.nbtCompound.let { compound ->
            _inputs.apply {
                clear()
                addAll(compound.getIntArray("inputs").map { MachineIoMode.byId(it) })
            }
            _outputs.apply {
                clear()
                addAll(compound.getIntArray("outputs").map { MachineIoMode.byId(it) })
            }
        }
        this.world.markBlockRangeForRenderUpdate(pos, pos)
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }

    override fun onRightClicked(toolType: ItemClayConfigTool.ToolType, player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (worldIn.isRemote) return
        when (toolType) {
            ItemClayConfigTool.ToolType.PIPING -> { /* handled by Block */ }
            ItemClayConfigTool.ToolType.INSERTION -> toggleInput(facing)
            ItemClayConfigTool.ToolType.EXTRACTION -> toggleOutput(facing)
            ItemClayConfigTool.ToolType.ROTATION -> {
                // facing is handled by Block
                //todo
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

    private fun refreshConnections() {
        //todo
    }

    companion object {
        fun create(tier: Int, validInputModes: List<MachineIoMode>, validOutputModes: List<MachineIoMode>): TileEntityMachine {
            return TileEntityMachine().apply {
                initParams(tier)
                setValidIoModes(validInputModes, validOutputModes)
            }
        }
    }
}