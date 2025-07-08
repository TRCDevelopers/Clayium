package com.github.trc.clayium.common.metatileentities

import com.github.trc.clayium.api.metatileentity.trait.ClayMarkerHandler
import com.github.trc.clayium.api.util.Cuboid6BlockPosIterator
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.config.ConfigCore
import com.github.trc.clayium.common.metatileentities.ActivatorMetaTileEntity.BlockEntityMode.BLOCK
import com.github.trc.clayium.common.metatileentities.ActivatorMetaTileEntity.BlockEntityMode.BLOCK_AND_ENTITY
import com.github.trc.clayium.common.metatileentities.ActivatorMetaTileEntity.BlockEntityMode.ENTITY
import com.github.trc.clayium.common.util.RayTraceMemory
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumActionResult
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class RangedActivatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ActivatorMetaTileEntity(metaTileEntityId, tier) {
    private val clayMarkerHandler = ClayMarkerHandler(this)

    override val maxBlocksPerTick: Int = ConfigCore.misc.rangedMinerMaxBlocksPerTick

    private val posIter: Cuboid6BlockPosIterator? by lazy {
        val range = clayMarkerHandler.markedRangeAbsolute?.copy() ?: return@lazy null
        Cuboid6BlockPosIterator(range)
    }

    private var rayTraceMemory: RayTraceMemory? = null

    override fun getNextBlockPos(): BlockPos? {
        val iter = posIter ?: return null
        val world = world ?: return null
        if (iter.hasNext()) return iter.next().toImmutable()

        if (this.repeatEnabled) {
            iter.restart()
            if (iter.hasNext()) return iter.next().toImmutable()
        }
        return null
    }

    override fun actionOnBlock(state: IBlockState, world: World, pos: BlockPos): EnumActionResult {
        val memory = this.rayTraceMemory
            ?: RayTraceMemory.getByFacing(this.frontFacing.opposite)
        when (this.blockEntityMode) {
            BLOCK -> if (this.raytrace) this.rayTraceBlock(world, pos, memory) else this.clickBlock(world, pos, memory)
            ENTITY -> TODO()
            BLOCK_AND_ENTITY -> TODO()
        }

        return EnumActionResult.SUCCESS
    }
}