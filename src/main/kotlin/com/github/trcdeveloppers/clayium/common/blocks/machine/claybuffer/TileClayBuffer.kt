package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.common.util.TierConstants
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import java.lang.IllegalArgumentException

class TileClayBuffer(
    tier: Int,
) : TileEntity(), ITickable {

    private val coolDown = TierConstants.getTransferCooldon(tier)

    // This does not use TierConstants because this only appears in this class.
    private val handler = ItemStackHandler(
        when (tier) {
            // todo: fixme
            1 -> 4
            2 -> 8
            3 -> 16
            in 4..13 -> 16
            else -> throw IllegalArgumentException("Invalid tier: $tier")
        }
    )

    private val importingFaces: MutableSet<EnumFacing> = HashSet()
    private val outputs: MutableSet<EnumFacing> = HashSet()
    private var ticked: Int = 0

    override fun update() {
        if (world.isRemote) return
        if (ticked < coolDown) {
            ticked++
            return
        }

        for (side in importingFaces) {
            val adjustHandler = this.world.getTileEntity(this.pos.offset(side))?.getCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue
            for (slot in 0 until adjustHandler.slots) {
                val importingStack = adjustHandler.getStackInSlot(slot)
                if (importingStack.isEmpty) continue
                val remainedStack = handler.insertItem(0, importingStack, false)
                adjustHandler.extractItem(slot, importingStack.count - remainedStack.count, false)
                break
            }
        }

        for (side in outputs) {
            val adjustHandler = this.world.getTileEntity(this.pos.offset(side))?.getCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue
            for (slot in 0 until handler.slots) {
                val exportingStack = handler.getStackInSlot(slot)
                if (exportingStack.isEmpty) continue
                val remainedStack = adjustHandler.insertItem(slot, exportingStack, false)
                handler.extractItem(slot, exportingStack.count - remainedStack.count, false)
                break
            }
        }

        ticked = 0
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        for (side in importingFaces) {
            compound.setBoolean("input_${side.name2}", true)
        }
        for (side in outputs) {
            compound.setBoolean("output_${side.name2}", true)
        }
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        for (side in EnumFacing.entries) {
            if (compound.getBoolean("input_${side.name2}")) importingFaces.add(side)
            if (compound.getBoolean("output_${side.name2}")) outputs.add(side)
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

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ITEM_HANDLER_CAPABILITY) ITEM_HANDLER_CAPABILITY as T else super.getCapability(capability, facing)
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }

    fun getInput(side: EnumFacing): Boolean {
        return side in importingFaces
    }
    fun getOutput(side: EnumFacing): Boolean {
        return side in outputs
    }

    fun toggleInput(side: EnumFacing) {
        if (side in importingFaces) importingFaces.remove(side) else importingFaces.add(side)
        this.markDirty()
    }
    fun toggleOutput(side: EnumFacing) {
        if (side in outputs) outputs.remove(side) else outputs.add(side)
        this.markDirty()
    }

    companion object {
        @JvmStatic
        private val ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}