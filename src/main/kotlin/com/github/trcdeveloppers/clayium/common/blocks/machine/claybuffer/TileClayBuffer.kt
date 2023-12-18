package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.common.blocks.machine.ItemStackTransferHandler
import com.github.trcdeveloppers.clayium.common.config.ConfigTierBalance
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
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
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

    private val handler = ItemStackHandler(inventoryX * inventoryY)

    private val importingFaces: MutableSet<EnumFacing> = HashSet()
    private val exportingFaces: MutableSet<EnumFacing> = HashSet()

    val inputs: BooleanArray get() = BooleanArray(6) { EnumFacing.entries[it] in importingFaces }
    val outputs: BooleanArray get() = BooleanArray(6) { EnumFacing.entries[it] in exportingFaces }

    private val itemStackTransferDelegation = ItemStackTransferHandler(
        ConfigTierBalance.bufferTransferIntervals[tier-1],
        ConfigTierBalance.bufferTransferAmount[tier-1],
        handler,
        importingFaces, exportingFaces,
        this,
    )

    override fun update() {
        itemStackTransferDelegation.transfer()
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
        return SPacketUpdateTileEntity(
            pos, 1,
            // We don't need to send the inventory data, since that is a container's job
            // Tier is also not needed, since it is not a dynamic property. It is only set once when the block is loaded
            NBTTagCompound().apply {
                for (side in importingFaces) {
                    setBoolean("input_${side.name2}", true)
                }
                for (side in exportingFaces) {
                    setBoolean("output_${side.name2}", true)
                }
            }
        )
    }

    @SideOnly(Side.CLIENT)
    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        val compound = pkt.nbtCompound
        for (side in EnumFacing.entries) {
            if (compound.getBoolean("input_${side.name2}")) importingFaces.add(side) else importingFaces.remove(side)
            if (compound.getBoolean("output_${side.name2}")) exportingFaces.add(side) else exportingFaces.remove(side)
        }
        this.world.markBlockRangeForRenderUpdate(pos, pos)
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

    //todo: cache
    fun getConnections(): BooleanArray {
        return BooleanArray(6) {
            val side = EnumFacing.entries[it]
            if (!(side in importingFaces || side in exportingFaces)) return@BooleanArray false
            this.world.getTileEntity(this.pos.offset(side))?.hasCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: false
        }
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