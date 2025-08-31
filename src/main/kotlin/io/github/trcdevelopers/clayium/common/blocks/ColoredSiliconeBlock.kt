package io.github.trcdevelopers.clayium.common.blocks

import io.github.trcdevelopers.clayium.api.block.ITieredBlock
import io.github.trcdevelopers.clayium.api.block.VariantBlock
import io.github.trcdevelopers.clayium.api.util.BlockMaterial
import io.github.trcdevelopers.clayium.api.util.ClayTiers
import net.minecraft.block.SoundType
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class ColoredSiliconeBlock : VariantBlock<EnumDyeColor>(BlockMaterial.IRON), ITieredBlock {
    init {
        soundType = SoundType.METAL
        setHardness(2.0f)
        setResistance(6.0f)
        setHarvestLevel("pickaxe", 0)
    }

    override fun getTier(stack: ItemStack) = ClayTiers.PRECISION
    override fun getTier(world: IBlockAccess, pos: BlockPos) = ClayTiers.PRECISION
}