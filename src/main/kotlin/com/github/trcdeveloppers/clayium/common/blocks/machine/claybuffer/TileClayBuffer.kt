package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.common.config.ConfigTierParameters
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
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import java.lang.IllegalArgumentException

class TileClayBuffer(
    tier: Int = 4,
) : TileEntity(), ITickable {

    var tier = tier
        private set

    val inventoryY: Int = when (tier) {
        in 4..7 -> tier - 3
        8, -> 4
        in 9..13 -> 6
        else -> throw IllegalArgumentException("Invalid tier for buffer: $tier")
    }

    val inventoryX: Int = when (tier) {
        in 4..7 -> tier - 2
        in 8..13 -> 9
        else -> throw IllegalArgumentException("Invalid tier for buffer: $tier")
    }

    private val transferInterval = try {
        ConfigTierParameters.bufferTransferIntervals[tier]
    } catch (e: IndexOutOfBoundsException) {
        throw IllegalArgumentException("Invalid tier: $tier")
    }

    private val handler = ItemStackHandler(inventoryX * inventoryY)

    private val importingFaces: MutableSet<EnumFacing> = HashSet()
    private val exportingFaces: MutableSet<EnumFacing> = HashSet()
    private var ticked: Int = 0

    val inputs: BooleanArray get() = BooleanArray(6) { EnumFacing.entries[it] in importingFaces }
    val outputs: BooleanArray get() = BooleanArray(6) { EnumFacing.entries[it] in exportingFaces }


    override fun update() {
//        if (world.isRemote) return
//        if (ticked < transferInterval) {
//            ticked++
//            return
//        }
//
//        for (side in importingFaces) {
//            val adjustHandler = this.world.getTileEntity(this.pos.offset(side))?.getCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue
//            this.transferStack(adjustHandler, handler, 64)
//        }
//
//        for (side in exportingFaces) {
//            val adjustHandler = this.world.getTileEntity(this.pos.offset(side))?.getCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue
//            this.transferStack(handler, adjustHandler, 64)
//        }
//        ticked = 0
    }

    private fun transferStack(from: IItemHandler, to: IItemHandler, amount: Int) {
        for (slot in 0 until from.slots) {
            val stack = from.getStackInSlot(slot)
            if (stack.isEmpty) continue
            val remainedStack = to.insertItem(slot, stack, false)
            from.extractItem(slot, stack.count - remainedStack.count, false)
            break
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setTag("inventory", handler.serializeNBT())
        compound.setInteger("tier", tier)
        for (side in importingFaces) {
            compound.setBoolean("input_${side.name2}", true)
        }
        for (side in exportingFaces) {
            compound.setBoolean("output_${side.name2}", true)
        }
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        handler.deserializeNBT(compound.getCompoundTag("inventory"))
        tier = compound.getInteger("tier")
        for (side in EnumFacing.entries) {
            if (compound.getBoolean("input_${side.name2}")) importingFaces.add(side)
            if (compound.getBoolean("output_${side.name2}")) exportingFaces.add(side)
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

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ITEM_HANDLER_CAPABILITY) handler as T else super.getCapability(capability, facing)
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }

    fun toggleInput(side: EnumFacing) {
        if (side in importingFaces) importingFaces.remove(side) else importingFaces.add(side)
        this.markDirty()
    }
    fun toggleOutput(side: EnumFacing) {
        if (side in exportingFaces) exportingFaces.remove(side) else exportingFaces.add(side)
        this.markDirty()
    }

    companion object {
        @JvmStatic
        private val ITEM_HANDLER_CAPABILITY: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}