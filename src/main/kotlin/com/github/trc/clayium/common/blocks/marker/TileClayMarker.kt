package com.github.trc.clayium.common.blocks.marker

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.metatileentity.SyncedTileEntityBase
import com.github.trc.clayium.common.config.ConfigCore
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

abstract class TileClayMarker : SyncedTileEntityBase() {

    /**
     * range of this marker.
     * null if not marked yet.
     */
    var rangeRelative: Cuboid6? = null
        private set

    fun onRightClick() {
        val mutPos = BlockPos.MutableBlockPos()
        val markerPoses = mutableListOf<BlockPos>()
        markerPoses.add(pos)
        for (side in EnumFacing.entries) {
            mutPos.setPos(this.pos)
            for (i in 1..ConfigCore.misc.clayMarkerMaxRange) {
                mutPos.move(side)
                if (world.getTileEntity(mutPos) is TileClayMarker) {
                    markerPoses.add(mutPos.toImmutable())
                    break
                }
            }
        }
        this.rangeRelative = constructRange(markerPoses)
    }

    /**
     * @return relative.
     */
    abstract fun constructRange(markerPoses: List<BlockPos>): Cuboid6

    override fun writeInitialSyncData(buf: PacketBuffer) {}
    override fun receiveInitialSyncData(buf: PacketBuffer) {}

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {}

    class NoExtend : TileClayMarker() {
        override fun constructRange(markerPoses: List<BlockPos>): Cuboid6 {
            return Cuboid6(
                    markerPoses.minOf { it.x }.toDouble(),
                    markerPoses.minOf { it.y }.toDouble(),
                    markerPoses.minOf { it.z }.toDouble(),
                    markerPoses.maxOf { it.x }.toDouble() + 1.0,
                    markerPoses.maxOf { it.y }.toDouble() + 1.0,
                    markerPoses.maxOf { it.z }.toDouble() + 1.0
            ).subtract(pos)
        }
    }
}