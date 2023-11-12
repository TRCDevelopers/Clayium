package com.github.trcdeveloppers.clayium.common.blocks.machine

import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TileSingleSlotMachine : TileEntity() {
    var inputUp = EnumImportMode.NONE
    var inputDown = EnumImportMode.NONE
    var inputNorth = EnumImportMode.NONE
    var inputSouth = EnumImportMode.NONE
    var inputWest = EnumImportMode.NONE
    var inputEast = EnumImportMode.NONE

    var outputUp = false
    var outputDown = false
    var outputNorth = false
    var outputSouth = false
    var outputWest = false
    var outputEast = false

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)

        compound.setString("inputUp", inputUp.name)
        compound.setString("inputDown", inputDown.name)
        compound.setString("inputNorth", inputNorth.name)
        compound.setString("inputSouth", inputSouth.name)
        compound.setString("inputWest", inputWest.name)
        compound.setString("inputEast", inputEast.name)

        compound.setBoolean("outputUp", outputUp)
        compound.setBoolean("outputDown", outputDown)
        compound.setBoolean("outputNorth", outputNorth)
        compound.setBoolean("outputSouth", outputSouth)
        compound.setBoolean("outputWest", outputWest)
        compound.setBoolean("outputEast", outputEast)

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        inputUp = EnumImportMode.valueOf(compound.getString("inputUp"))
        inputDown = EnumImportMode.valueOf(compound.getString("inputDown"))
        inputNorth = EnumImportMode.valueOf(compound.getString("inputNorth"))
        inputSouth = EnumImportMode.valueOf(compound.getString("inputSouth"))
        inputWest = EnumImportMode.valueOf(compound.getString("inputWest"))
        inputEast = EnumImportMode.valueOf(compound.getString("inputEast"))

        outputUp = compound.getBoolean("outputUp")
        outputDown = compound.getBoolean("outputDown")
        outputNorth = compound.getBoolean("outputNorth")
        outputSouth = compound.getBoolean("outputSouth")
        outputWest = compound.getBoolean("outputWest")
        outputEast = compound.getBoolean("outputEast")
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

    fun toggleInput(side: EnumFacing) {
        when (side) {
            EnumFacing.UP -> inputUp = inputUp.next
            EnumFacing.DOWN -> inputDown = inputDown.next
            EnumFacing.NORTH -> inputNorth = inputNorth.next
            EnumFacing.SOUTH -> inputSouth = inputSouth.next
            EnumFacing.WEST -> inputWest = inputWest.next
            EnumFacing.EAST -> inputEast = inputEast.next
        }
        markDirty()
    }

    fun toggleOutput(side: EnumFacing) {
        when (side) {
            EnumFacing.UP -> outputUp = !outputUp
            EnumFacing.DOWN -> outputDown = !outputDown
            EnumFacing.NORTH -> outputNorth = !outputNorth
            EnumFacing.SOUTH -> outputSouth = !outputSouth
            EnumFacing.WEST -> outputWest = !outputWest
            EnumFacing.EAST -> outputEast = !outputEast
        }
        markDirty()
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }
}