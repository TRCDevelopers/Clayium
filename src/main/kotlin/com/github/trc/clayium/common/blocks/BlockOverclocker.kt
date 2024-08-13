package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.block.IOverclockerBlock
import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.block.VariantBlock
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.ITier
import net.minecraft.block.material.Material
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class BlockOverclocker : VariantBlock<BlockOverclocker.BlockType>(Material.IRON), IOverclockerBlock, ITieredBlock {

    override fun getOverclockFactor(world: IBlockAccess, pos: BlockPos): Double {
        return getOcFactor(getEnum(world.getBlockState(pos)))
    }

    private fun getOcFactor(type: BlockType): Double {
        return when (type) {
            BlockType.ANTIMATTER -> 1.5
            BlockType.PURE_ANTIMATTER -> 2.3
            BlockType.OEC -> 3.5
            BlockType.OPA -> 5.0
        }
    }

    override fun getTier(stack: ItemStack): ITier {
        return getEnum(stack).tier
    }

    override fun getTier(world: IBlockAccess, pos: BlockPos): ITier {
        return getEnum(world.getBlockState(pos)).tier
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        tooltip.add(I18n.format("tile.clayium.overclocker.factor", getOcFactor(getEnum(stack))))
    }

    enum class BlockType(val tier: ITier) : IStringSerializable {
        ANTIMATTER(ClayTiers.ANTIMATTER),
        PURE_ANTIMATTER(ClayTiers.PURE_ANTIMATTER),
        OEC(ClayTiers.OEC),
        OPA(ClayTiers.OPA);
        override fun getName() = name.lowercase()
    }
}