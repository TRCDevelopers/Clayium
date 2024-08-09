package com.github.trc.clayium.common.blocks.claytree

import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.getAsItem
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockPlanks
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

@Suppress("OVERRIDE_DEPRECATION")
class BlockClayLeaves : BlockLeaves(), ITieredBlock {
    init {
        defaultState = blockState.baseState.withProperty(CHECK_DECAY, true).withProperty(DECAYABLE, true)
    }
    override fun getWoodType(meta: Int) = BlockPlanks.EnumType.OAK
    override fun onSheared(item: ItemStack, world: IBlockAccess?, pos: BlockPos?, fortune: Int): List<ItemStack> {
        return listOf(ItemStack(this.getAsItem()))
    }

    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(CHECK_DECAY, meta and 0b01 != 0).withProperty(DECAYABLE, meta and 0b10 != 0)
    override fun getMetaFromState(state: IBlockState) = (if (state.getValue(CHECK_DECAY)) 0b01 else 0) or (if (state.getValue(DECAYABLE)) 0b10 else 0)

    override fun createBlockState() = BlockStateContainer(this, CHECK_DECAY, DECAYABLE)

    override fun getTier(stack: ItemStack) = ClayTiers.CLAY_STEEL
    override fun getTier(world: IBlockAccess, pos: BlockPos) = ClayTiers.CLAY_STEEL

    override fun getRenderLayer() = Blocks.LEAVES.defaultState.block.getRenderLayer()
    override fun isOpaqueCube(state: IBlockState) = Blocks.LEAVES.defaultState.isOpaqueCube
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing) = Blocks.LEAVES.defaultState.shouldSideBeRendered(blockAccess, pos, side)
}