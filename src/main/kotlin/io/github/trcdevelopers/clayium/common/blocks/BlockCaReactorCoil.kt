package io.github.trcdevelopers.clayium.common.blocks

import io.github.trcdevelopers.clayium.api.block.ITieredBlock
import io.github.trcdevelopers.clayium.api.block.VariantBlock
import io.github.trcdevelopers.clayium.api.util.ClayTiers
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockCaReactorCoil : VariantBlock<BlockCaReactorCoil.BlockType>(Material.IRON), ITieredBlock {
    init {
        creativeTab = ClayiumCTabs.main
        setHardness(2.0f)
        setResistance(5.0f)
        setHarvestLevel("pickaxe", 0)
        setSoundType(SoundType.METAL)
    }

    override fun getTier(stack: ItemStack) = getEnum(stack).tier
    override fun getTier(world: IBlockAccess, pos: BlockPos) = getEnum(world.getBlockState(pos)).tier

    enum class BlockType(val tier: ITier) : IStringSerializable {
        ANTIMATTER(ClayTiers.ANTIMATTER),
        PURE_ANTIMATTER(ClayTiers.PURE_ANTIMATTER),
        OEC(ClayTiers.OEC),
        OPA(ClayTiers.OPA),
        ;

        override fun getName() = name.lowercase()
    }
}