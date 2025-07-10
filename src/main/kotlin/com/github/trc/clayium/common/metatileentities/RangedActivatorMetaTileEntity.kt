package com.github.trc.clayium.common.metatileentities

import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IRayTraceMemoryApplicable
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.ClayMarkerHandler
import com.github.trc.clayium.api.util.BlockPosIterator
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.config.ConfigCore
import com.github.trc.clayium.common.util.RayTraceMemory
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

class RangedActivatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    machineName: String,
) : ActivatorMetaTileEntity(metaTileEntityId, tier, machineName), IRayTraceMemoryApplicable {
    private val clayMarkerHandler = ClayMarkerHandler(this)

    override val maxBlocksPerTick: Int = ConfigCore.misc.rangedMachineMaxBlocksPerTick
    override val rangeRelativeClient get() = clayMarkerHandler.renderingRangeRelative

    private val posIter by lazy {
        val range = clayMarkerHandler.markedRangeAbsolute?.copy() ?: return@lazy null
        BlockPosIterator(range)
    }

    private var rayTraceMemory: RayTraceMemory? = null

    override fun getNextBlockPos(): BlockPos? {
        val iter = posIter ?: return null
        if (iter.hasNext()) return iter.next()

        if (this.repeatEnabled) {
            this.allBlocksProcessed = true
            iter.restart()
            if (iter.hasNext()) return iter.next()
        }
        return null
    }

    override fun actionOnBlock(state: IBlockState, world: World, pos: BlockPos): EnumActionResult {
        val memory = this.rayTraceMemory
            ?: RayTraceMemory.getByFacing(this.frontFacing.opposite)
        val (minPos, maxPos) = this.clayMarkerHandler.markedRangeAbsolute
            ?: return EnumActionResult.FAIL
        val rangeAabb = AxisAlignedBB(minPos, maxPos.add(1, 1, 1))
        this.doWork(world, pos, memory, rangeAabb)
        return EnumActionResult.SUCCESS
    }

    override fun acceptRayTraceMemory(rayTraceMemory: RayTraceMemory): Boolean {
        this.rayTraceMemory = rayTraceMemory
        return true
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.RAY_TRACE_MEMORY_APPLICABLE) {
            return capability.cast(this)
        }
        return super.getCapability(capability, facing)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return RangedActivatorMetaTileEntity(metaTileEntityId, tier, "ranged_activator")
    }
}