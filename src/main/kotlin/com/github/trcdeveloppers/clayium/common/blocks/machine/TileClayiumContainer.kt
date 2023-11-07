package com.github.trcdeveloppers.clayium.common.blocks.machine

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

    var upIn = EnumInsertionModeSingle.NONE
        private set
    var downIn = EnumInsertionModeSingle.NONE
        private set
    var northIn = EnumInsertionModeSingle.NONE
        private set
    var southIn = EnumInsertionModeSingle.NONE
        private set
    var eastIn = EnumInsertionModeSingle.NONE
        private set
    var westIn = EnumInsertionModeSingle.NONE
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

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setString("upIn", upIn.name)
        compound.setString("downIn", downIn.name)
        compound.setString("northIn", northIn.name)
        compound.setString("southIn", southIn.name)
        compound.setString("eastIn", eastIn.name)
        compound.setString("westIn", westIn.name)
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
        upIn = EnumInsertionModeSingle.valueOf(compound.getString("upIn"))
        downIn = EnumInsertionModeSingle.valueOf(compound.getString("downIn"))
        northIn = EnumInsertionModeSingle.valueOf(compound.getString("northIn"))
        southIn = EnumInsertionModeSingle.valueOf(compound.getString("southIn"))
        eastIn = EnumInsertionModeSingle.valueOf(compound.getString("eastIn"))
        westIn = EnumInsertionModeSingle.valueOf(compound.getString("westIn"))
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

    fun toggleInsertion(facing: EnumFacing) {
        when (facing) {
            EnumFacing.UP -> this.upIn = this.upIn.next
            EnumFacing.DOWN -> this.downIn = this.downIn.next
            EnumFacing.NORTH -> this.northIn = this.northIn.next
            EnumFacing.SOUTH -> this.southIn = this.southIn.next
            EnumFacing.EAST -> this.eastIn = this.eastIn.next
            EnumFacing.WEST -> this.westIn = this.westIn.next
        }
    }

    fun toggleExtraction(facing: EnumFacing) {
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
