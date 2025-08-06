package io.github.trcdevelopers.clayium.common.blocks.marker

import codechicken.lib.vec.Cuboid6
import io.github.trcdevelopers.clayium.api.util.next
import io.github.trcdevelopers.clayium.client.renderer.AreaMarkerRenderer.RangeRenderMode
import io.github.trcdevelopers.clayium.common.config.ConfigCore
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

abstract class TileClayMarker : TileEntity() {

    /**
     * A Cuboid6 instance for rendering. Inclusive. null if not marked yet.
     * Use [rangeAbs] if you want to write a server-side logic.
     */
    var renderingRangeRelative: Cuboid6? = null
        private set

    /**
     * absolute range of this marker.
     * represented by two BlockPoses, the first is the minimum corner and the second is the maximum corner.
     * null if not marked yet.
     */
    var rangeAbs: Pair<BlockPos, BlockPos>? = null
        private set

    val markerPoses = mutableListOf<BlockPos>()

    var rangeRenderMode = RangeRenderMode.DISABLED

    //todo remove logic from client
    fun onRightClick() {
        rangeRenderMode = rangeRenderMode.next()
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
        val rangeAbsolute = constructRange(markerPoses)
        this.rangeAbs = rangeAbsolute
        this.renderingRangeRelative = Cuboid6(rangeAbsolute.first, rangeAbsolute.second.add(1, 1, 1))
            .subtract(this.pos)
        this.markerPoses.clear()
        this.markerPoses.addAll(markerPoses)
    }

    /**
     * @return Absolute.
     */
    abstract fun constructRange(markerPoses: List<BlockPos>): Pair<BlockPos, BlockPos>

    override fun getRenderBoundingBox(): AxisAlignedBB {
        return INFINITE_EXTENT_AABB
    }

    open class NoExtend : TileClayMarker() {
        override fun constructRange(markerPoses: List<BlockPos>): Pair<BlockPos, BlockPos> {
            val min = BlockPos(
                markerPoses.minOf { it.x },
                markerPoses.minOf { it.y },
                markerPoses.minOf { it.z }
            )
            val max = BlockPos(
                markerPoses.maxOf { it.x },
                markerPoses.maxOf { it.y },
                markerPoses.maxOf { it.z }
            )
            return Pair(min, max)
        }
    }

    class ExtendToGround : NoExtend() {
        override fun constructRange(markerPoses: List<BlockPos>): Pair<BlockPos, BlockPos> {
            val superVal = super.constructRange(markerPoses)
            val min = BlockPos(superVal.first.x, 0, superVal.first.z)
            return Pair(min, superVal.second)
        }
    }

    class ExtendToSky : NoExtend() {
        override fun constructRange(markerPoses: List<BlockPos>): Pair<BlockPos, BlockPos> {
            val superVal = super.constructRange(markerPoses)
            val max = BlockPos(superVal.second.x, 255, superVal.second.z)
            return Pair(superVal.first, max)
        }
    }

    class AllHeight : NoExtend() {
        override fun constructRange(markerPoses: List<BlockPos>): Pair<BlockPos, BlockPos> {
            val superVal = super.constructRange(markerPoses)
            val min = BlockPos(superVal.first.x, 0, superVal.first.z)
            val max = BlockPos(superVal.second.x, 255, superVal.second.z)
            return Pair(min, max)
        }
    }
}