package com.github.trc.clayium.common.blocks.claytree

import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.util.ClayTiers
import net.minecraft.block.BlockRotatedPillar
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockClayLog : BlockRotatedPillar(Material.WOOD), ITieredBlock {
    init {
        setHardness(1.5f)
        setSoundType(SoundType.GROUND)
        setHarvestLevel("axe", 0)
        defaultState = blockState.baseState.withProperty(AXIS, EnumFacing.Axis.Y)
    }

    override fun canSustainLeaves(state: IBlockState, world: IBlockAccess, pos: BlockPos) = true
    override fun isWood(world: IBlockAccess, pos: BlockPos) = true

    override fun getTier(stack: ItemStack) = ClayTiers.CLAY_STEEL
    override fun getTier(world: IBlockAccess, pos: BlockPos) = ClayTiers.CLAY_STEEL
}