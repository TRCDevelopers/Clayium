package com.github.trc.clayium.common.blocks.marker

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.common.config.ConfigCore
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

abstract class TileClayMarker : TileEntity() {

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
            .subtract(pos)
    }

    /**
     * @return Absolute.
     */
    abstract fun constructRange(markerPoses: List<BlockPos>): Cuboid6

    override fun getRenderBoundingBox(): AxisAlignedBB {
        return INFINITE_EXTENT_AABB
    }

    open class NoExtend : TileClayMarker() {
        override fun constructRange(markerPoses: List<BlockPos>): Cuboid6 {
            return Cuboid6(
                    markerPoses.minOf { it.x }.toDouble(),
                    markerPoses.minOf { it.y }.toDouble(),
                    markerPoses.minOf { it.z }.toDouble(),
                    markerPoses.maxOf { it.x }.toDouble() + 1.0,
                    markerPoses.maxOf { it.y }.toDouble() + 1.0,
                    markerPoses.maxOf { it.z }.toDouble() + 1.0
            )
        }
    }

    class ExtendToGround : NoExtend() {
        override fun constructRange(markerPoses: List<BlockPos>): Cuboid6 {
            return super.constructRange(markerPoses)
                .apply { min.y = 0.0 }
        }
    }

    class ExtendToSky : NoExtend() {
        override fun constructRange(markerPoses: List<BlockPos>): Cuboid6 {
            return super.constructRange(markerPoses)
                .apply { max.y = 255.0 }
        }
    }

    class AllHeight : NoExtend() {
        override fun constructRange(markerPoses: List<BlockPos>): Cuboid6 {
            return super.constructRange(markerPoses)
                .apply { min.y = 0.0; max.y = 255.0 }
        }
    }
}