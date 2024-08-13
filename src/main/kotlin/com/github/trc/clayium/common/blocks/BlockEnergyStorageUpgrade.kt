package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.block.IEnergyStorageUpgradeBlock
import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.block.VariantBlock
import net.minecraft.block.material.Material
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class BlockEnergyStorageUpgrade : VariantBlock<BlockOverclocker.BlockType>(Material.IRON), IEnergyStorageUpgradeBlock, ITieredBlock {

    override fun getExtraStackLimit(world: IBlockAccess, pos: BlockPos) = getExtraStackLimit(getEnum(world.getBlockState(pos)))

    override fun getTier(stack: ItemStack) = getEnum(stack).tier
    override fun getTier(world: IBlockAccess, pos: BlockPos) = getEnum(world.getBlockState(pos)).tier

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: List<String?>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    private fun getExtraStackLimit(type: BlockOverclocker.BlockType): Int {
        return when (type) {
            BlockOverclocker.BlockType.ANTIMATTER -> 2
            BlockOverclocker.BlockType.PURE_ANTIMATTER -> 4
            BlockOverclocker.BlockType.OEC -> 8
            BlockOverclocker.BlockType.OPA -> 64
        }
    }

    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED
}