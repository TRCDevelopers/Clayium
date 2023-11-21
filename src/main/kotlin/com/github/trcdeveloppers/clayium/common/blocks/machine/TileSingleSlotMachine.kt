package com.github.trcdeveloppers.clayium.common.blocks.machine

import com.github.trcdeveloppers.clayium.Clayium
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TileSingleSlotMachine : TileEntity() {

    private val inputs = Array(6) { EnumIoMode.NONE }
    private val outputs = Array(6) { false }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)

        for (side in EnumFacing.entries) {
            compound.setInteger("input${side.name}", inputs[side.index].index)
            compound.setBoolean("output${side.name}", outputs[side.index])
        }

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        for (side in EnumFacing.entries) {
            inputs[side.index] = EnumIoMode.byIndex(compound.getInteger("input${side.name}"))
            outputs[side.index] = compound.getBoolean("output${side.name}")
        }
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(pos, 1, this.writeToNBT(NBTTagCompound()))
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        this.readFromNBT(pkt.nbtCompound)
        if (this.world.isRemote) {
            this.world.markBlockRangeForRenderUpdate(pos, pos)
        }
    }

    fun getInput(side: EnumFacing): EnumIoMode {
        return inputs[side.index]
    }

    fun getOutput(side: EnumFacing): Boolean {
        return outputs[side.index]
    }

    fun toggleInput(side: EnumFacing) {
        inputs[side.index] = inputs[side.index].next
        markDirty()
    }

    fun toggleOutput(side: EnumFacing) {
        outputs[side.index] = !outputs[side.index]
        markDirty()
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }
}