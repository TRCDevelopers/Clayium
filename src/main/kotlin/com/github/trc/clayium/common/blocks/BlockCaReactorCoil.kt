package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.block.VariantBlock
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
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