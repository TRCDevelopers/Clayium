package io.github.trcdevelopers.clayium.common.blocks

import io.github.trcdevelopers.clayium.api.block.ITieredBlock
import io.github.trcdevelopers.clayium.api.block.VariantBlock
import io.github.trcdevelopers.clayium.api.util.ClayTiers
import io.github.trcdevelopers.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

@Suppress("OVERRIDE_DEPRECATION")
class BlockMachineHull : VariantBlock<ClayTiers>(Material.IRON), ITieredBlock {

    init {
        creativeTab = ClayiumCTabs.main
        setHardness(2.0f)
        setResistance(5.0f)
        setHarvestLevel("pickaxe", 0)
        setSoundType(SoundType.METAL)
    }

    override fun getTier(stack: ItemStack) = getEnum(stack)
    override fun getTier(world: IBlockAccess, pos: BlockPos) = getEnum(world.getBlockState(pos))
}