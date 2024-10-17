package com.github.trc.clayium.common.blocks.claytree

import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import net.minecraft.block.BlockBush
import net.minecraft.block.BlockSapling.STAGE
import net.minecraft.block.IGrowable
import net.minecraft.block.SoundType
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenTrees
import net.minecraftforge.common.util.Constants
import net.minecraftforge.event.terraingen.TerrainGen
import java.util.Random

@Suppress("OVERRIDE_DEPRECATION")
class BlockClaySapling : BlockBush(), IGrowable, ITieredBlock {
    val clayTreeGen =
        WorldGenTrees(
            true,
            4,
            ClayiumBlocks.CLAY_TREE_LOG.defaultState,
            ClayiumBlocks.CLAY_TREE_LEAVES.defaultState,
            false
        )
    val saplingAabb =
        AxisAlignedBB(
            0.09999999403953552,
            0.0,
            0.09999999403953552,
            0.8999999761581421,
            0.800000011920929,
            0.8999999761581421
        )

    init {
        setHardness(0.0f)
        setSoundType(SoundType.PLANT)
        defaultState = blockState.baseState.withProperty(STAGE, 0)
    }

    override fun createBlockState() = BlockStateContainer(this, STAGE)

    override fun getMetaFromState(state: IBlockState) = state.getValue(STAGE)

    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(STAGE, meta)

    override fun canGrow(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        isClient: Boolean
    ): Boolean {
        return true
    }

    override fun canUseBonemeal(
        worldIn: World,
        rand: Random,
        pos: BlockPos,
        state: IBlockState
    ): Boolean {
        return rand.nextDouble() < 0.45
    }

    override fun grow(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState) {
        if (state.getValue(STAGE) == 0) {
            worldIn.setBlockState(pos, state.cycleProperty(STAGE), Constants.BlockFlags.NO_RERENDER)
        } else {
            this.generateTree(worldIn, pos, rand)
        }
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        // same as BlockSapling
        if (worldIn.isRemote) return

        super.updateTick(worldIn, pos, state, rand)
        if (!worldIn.isAreaLoaded(pos, 1)) return
        if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
            this.grow(worldIn, rand, pos, state)
        }
    }

    fun generateTree(worldIn: World, pos: BlockPos, rand: Random) {
        if (!TerrainGen.saplingGrowTree(worldIn, rand, pos)) return
        worldIn.setBlockState(pos, Blocks.AIR.defaultState, Constants.BlockFlags.NO_RERENDER)
        clayTreeGen.generate(worldIn, rand, pos)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) =
        saplingAabb

    override fun getTier(stack: ItemStack) = ClayTiers.CLAY_STEEL

    override fun getTier(world: IBlockAccess, pos: BlockPos) = ClayTiers.CLAY_STEEL
}
