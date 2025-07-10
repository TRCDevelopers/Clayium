package com.github.trc.clayium.api.metatileentity.trait

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_AREA_RANGE
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.blocks.marker.TileClayMarker
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.util.Constants

class ClayMarkerHandler(
    metaTileEntity: MetaTileEntity
) : MTETrait(metaTileEntity, ClayiumDataCodecs.CLAY_MARKER_HANDLER) {

    /**
     * available after onPlacement.
     * null if no neighbor marker.
     * once set, never change (on the serve side).
     */
    var markedRangeAbsolute: Pair<BlockPos, BlockPos>? = null
        private set
    var markedRangeAbsoluteAabb: AxisAlignedBB? = null
        private set

    val renderingRangeRelative get() = markedRangeAbsolute?.let { (minPos, maxPos) ->
        Cuboid6(minPos, maxPos.add(1.0, 1.0, 1.0))
            .subtract(metaTileEntity.pos)
    }

    override fun onPlacement() {
        val range = this.getRangeFromNeighborMarker()
        if (range != null) {
            this.markedRangeAbsolute = range
            val (min, max) = range
            this.markedRangeAbsoluteAabb = AxisAlignedBB(min, max.add(1.0, 1.0, 1.0))
        }
        writeMarkedRange()
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        writeMarkedRange()
    }

    private fun writeMarkedRange() {
        writeCustomData(UPDATE_AREA_RANGE) {
            val range = markedRangeAbsolute
            if (range == null) {
                writeBoolean(false)
            } else {
                writeBoolean(true)
                val (minPos, maxPos) = range
                writeLong(minPos.toLong())
                writeLong(maxPos.toLong())
            }
        }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == UPDATE_AREA_RANGE) {
            val hasRange = buf.readBoolean()
            if (hasRange) {
                val minPos = BlockPos.fromLong(buf.readLong())
                val maxPos = BlockPos.fromLong(buf.readLong())
                this.markedRangeAbsolute = Pair(minPos, maxPos)
            } else {
                this.markedRangeAbsolute = null
            }
        }
    }

    /**
     * null if no neighbor marker.
     */
    private fun getRangeFromNeighborMarker(): Pair<BlockPos, BlockPos>? {
        val world = this.metaTileEntity.world ?: return null
        if (world.isRemote) return null
        for (side in EnumFacing.entries) {
            val pos = this.metaTileEntity.pos?.offset(side) ?: continue
            val tileEntity = world.getTileEntity(pos)
            if (tileEntity !is TileClayMarker) continue

            val absRange = tileEntity.rangeAbs ?: continue
            for (markerPos in tileEntity.markerPoses) {
                world.destroyBlock(markerPos, true)
            }
            return absRange
        }
        return null
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = super.serializeNBT()
        val range = this.markedRangeAbsolute
        if (range != null) {
            data.setLong("minPos", range.first.toLong())
            data.setLong("maxPos", range.second.toLong())
        }
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        if (data.hasKey("minPos", Constants.NBT.TAG_LONG) && data.hasKey("maxPos", Constants.NBT.TAG_LONG)) {
            val minPos = BlockPos.fromLong(data.getLong("minPos"))
            val maxPos = BlockPos.fromLong(data.getLong("maxPos"))
            this.markedRangeAbsolute = Pair(minPos, maxPos)
            this.markedRangeAbsoluteAabb = AxisAlignedBB(minPos, maxPos.add(1.0, 1.0, 1.0))
        }
    }
}