package com.github.trcdeveloppers.clayium.common.blocks.machine

import com.github.trcdeveloppers.clayium.Clayium
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TileClayiumContainer(private val block: Block) : TileEntity() {

    var upIn = false
        private set
    var downIn = false
        private set
    var northIn = false
        private set
    var southIn = false
        private set
    var eastIn = false
        private set
    var westIn = false
        private set

    var upEx = false
        private set
    var downEx = false
        private set
    var northEx = false
        private set
    var southEx = false
        private set
    var eastEx = false
        private set
    var westEx = false
        private set

    fun toggleUpIn() {
        Clayium.LOGGER.info("toggleUpIn called, before: $upIn")
        this.upIn = !this.upIn
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setBoolean("upIn", upIn)
        compound.setBoolean("downIn", downIn)
        compound.setBoolean("northIn", northIn)
        compound.setBoolean("southIn", southIn)
        compound.setBoolean("eastIn", eastIn)
        compound.setBoolean("westIn", westIn)
        compound.setBoolean("upDown", upEx)
        compound.setBoolean("downDown", downEx)
        compound.setBoolean("northDown", northEx)
        compound.setBoolean("southDown", southEx)
        compound.setBoolean("eastDown", eastEx)
        compound.setBoolean("westDown", westEx)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        upIn = compound.getBoolean("upIn")
        downIn = compound.getBoolean("downIn")
        northIn = compound.getBoolean("northIn")
        southIn = compound.getBoolean("southIn")
        eastIn = compound.getBoolean("eastIn")
        westIn = compound.getBoolean("westIn")
        upEx = compound.getBoolean("upDown")
        downEx = compound.getBoolean("downDown")
        northEx = compound.getBoolean("northDown")
        southEx = compound.getBoolean("southDown")
        eastEx = compound.getBoolean("eastDown")
        westEx = compound.getBoolean("westDown")
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(pos, 1, this.writeToNBT(NBTTagCompound()))
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        this.readFromNBT(pkt.nbtCompound)
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return newSate.block !== this.block
    }

    fun cycleInput(facing: EnumFacing) {
        when (facing) {
            EnumFacing.UP -> this.upIn = !this.upIn
            EnumFacing.DOWN -> this.downIn = !this.downIn
            EnumFacing.NORTH -> this.northIn = !this.northIn
            EnumFacing.SOUTH -> this.southIn = !this.southIn
            EnumFacing.EAST -> this.eastIn = !this.eastIn
            EnumFacing.WEST -> this.westIn = !this.westIn
        }
    }

    fun cycleOutput(facing: EnumFacing) {
        when (facing) {
            EnumFacing.UP -> this.upEx = !this.upEx
            EnumFacing.DOWN -> this.downEx = !this.downEx
            EnumFacing.NORTH -> this.northEx = !this.northEx
            EnumFacing.SOUTH -> this.southEx = !this.southEx
            EnumFacing.EAST -> this.eastEx = !this.eastEx
            EnumFacing.WEST -> this.westEx = !this.westEx
        }
    }
}
