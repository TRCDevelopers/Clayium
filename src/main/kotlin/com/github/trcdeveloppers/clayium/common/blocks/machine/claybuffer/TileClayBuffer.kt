package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TileClayBuffer : TileEntity(), ITickable {

    private val inputs = Array(6) { false }
    private val outputs = Array(6) { false }

    override fun update() {
        if (world.isRemote) return
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        for (side in EnumFacing.entries) {
            compound.setBoolean("input_${side.name2}", inputs[side.index])
            compound.setBoolean("output_${side.name2}", outputs[side.index])
        }
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        for (side in EnumFacing.entries) {
            inputs[side.index] = compound.getBoolean("input_${side.name2}")
            outputs[side.index] = compound.getBoolean("output_${side.name2}")
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

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }

    fun getInput(side: EnumFacing): Boolean {
        return inputs[side.index]
    }
    fun getOutput(side: EnumFacing): Boolean {
        return outputs[side.index]
    }

    fun toggleInput(side: EnumFacing) {
        inputs[side.index] = !inputs[side.index]
        this.markDirty()
    }
    fun toggleOutput(side: EnumFacing) {
        outputs[side.index] = !outputs[side.index]
        this.markDirty()
    }
}