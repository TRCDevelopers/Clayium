package com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.ItemStackTransferHandler
import com.github.trcdevelopers.clayium.common.config.config.ConfigTierBalance
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

class TileClayBuffer() : TileEntity(), ITickable {

    constructor(tier: Int) : this() {
        this.initParams(tier)
    }

    var tier = -1
        private set

    var inventoryY: Int = -1
        private set
    var inventoryX: Int = -1
        private set

    private lateinit var handler: ItemStackHandler
    private lateinit var itemStackTransferDelegation : ItemStackTransferHandler

    private val importingFaces: MutableSet<EnumFacing> = HashSet()
    private val exportingFaces: MutableSet<EnumFacing> = HashSet()

    val inputs: BooleanArray get() = BooleanArray(6) { EnumFacing.entries[it] in importingFaces }
    val outputs: BooleanArray get() = BooleanArray(6) { EnumFacing.entries[it] in exportingFaces }

    val connections = BooleanArray(6)

    private fun initParams(tierIn: Int) {
        this.tier = tierIn
        this.inventoryY = when (tierIn) {
            in 4..7 -> tierIn - 3
            8, -> 4
            in 9..13 -> 6
            else -> throw IllegalArgumentException("Invalid tier for buffer: $tierIn")
        }
        this.inventoryX = when (tierIn) {
            in 4..7 -> tierIn - 2
            in 8..13 -> 9
            else -> throw IllegalArgumentException("Invalid tier for buffer: $tierIn")
        }
        this.handler = ItemStackHandler(inventoryX * inventoryY)
        this.itemStackTransferDelegation = ItemStackTransferHandler(
            ConfigTierBalance.bufferTransferIntervals[tierIn - 1],
            ConfigTierBalance.bufferTransferAmount[tierIn - 1],
            handler,
            importingFaces, exportingFaces,
            this,
        )
    }

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

        this.tier = compound.getInteger("tier")
        this.initParams(tier)
        Clayium.LOGGER.info("Reading buffer with tier $tier")

        handler.deserializeNBT(compound.getCompoundTag("inventory"))
        for (side in EnumFacing.entries) {
            if (compound.getBoolean("input_${side.name2}")) importingFaces.add(side)
            if (compound.getBoolean("output_${side.name2}")) exportingFaces.add(side)
        }
    }

    override fun getUpdateTag(): NBTTagCompound {
        this.refreshConnections()
        return this.writeToNBT(
            NBTTagCompound().apply {
                setByteArray("connections", ByteArray(6) { if (connections[it]) 1 else 0 })
            }
        )
    }

    override fun handleUpdateTag(tag: NBTTagCompound) {
        super.handleUpdateTag(tag)
        val connections = tag.getByteArray("connections")
        for (i in connections.indices) {
            this.connections[i] = connections[i] == 1.toByte()
        }
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        this.refreshConnections()
        return SPacketUpdateTileEntity(
            pos, 0,
            // We don't need to send the inventory data, since that is a container's job
            // Tier is also not needed, since it is not a dynamic property. It is only set once when the block is loaded
            NBTTagCompound().apply {
                for (side in importingFaces) {
                    setBoolean("input_${side.name2}", true)
                }
                for (side in exportingFaces) {
                    setBoolean("output_${side.name2}", true)
                }
                setByteArray("connections", ByteArray(6) { if (connections[it]) 1 else 0 })
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
        val connections = compound.getByteArray("connections")
        for (i in connections.indices) {
            this.connections[i] = connections[i] == 1.toByte()
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

    fun toggleInput(side: EnumFacing) {
        if (side in importingFaces) importingFaces.remove(side) else importingFaces.add(side)
        this.markDirty()
    }

    fun toggleOutput(side: EnumFacing) {
        if (side in exportingFaces) exportingFaces.remove(side) else exportingFaces.add(side)
        this.markDirty()
    }

    fun refreshConnections() {
        for (side in EnumFacing.entries) {
            val i = side.index
            val j = side.opposite.index
            val tile = this.world.getTileEntity(this.pos.offset(side))
            if (tile is TileClayBuffer) {
                this.connections[i] = (this.inputs[i] || this.outputs[i]) || (tile.inputs[j] || tile.outputs[j])
            } else {
                this.connections[i] = tile?.hasCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: false
            }
        }
    }

    fun rotate() {
        importingFaces.clear()
        importingFaces.addAll(importingFaces.map { it.opposite })

        exportingFaces.clear()
        exportingFaces.addAll(exportingFaces.map { it.opposite })
    }

    companion object {
        @JvmStatic
        private val ITEM_HANDLER_CAPABILITY: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}