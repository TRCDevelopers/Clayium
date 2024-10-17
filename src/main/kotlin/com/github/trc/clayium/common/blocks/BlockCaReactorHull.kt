package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.block.ICaReactorHull
import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

@Suppress("OVERRIDE_DEPRECATION")
class BlockCaReactorHull : Block(Material.IRON), ICaReactorHull, ITieredBlock {
    init {
        creativeTab = ClayiumCTabs.main
        setHardness(2.0f)
        setResistance(5.0f)
        setHarvestLevel("pickaxe", 0)
        setSoundType(SoundType.METAL)
    }

    override fun getTier(stack: ItemStack) = getTier(stack.metadata.coerceIn(0..9))

    override fun getTier(world: IBlockAccess, pos: BlockPos) =
        getTier(world.getBlockState(pos).getValue(META))

    private fun getTier(meta: Int): ITier {
        return when (meta) {
            0 -> ClayTiers.ANTIMATTER
            in 1..4 -> ClayTiers.PURE_ANTIMATTER
            in 5..8 -> ClayTiers.OEC
            9 -> ClayTiers.OPA
            else -> ClayTiers.DEFAULT
        }
    }

    override fun getCaRank(stack: ItemStack) = stack.metadata + 1

    override fun getCaRank(world: IBlockAccess, pos: BlockPos) =
        world.getBlockState(pos).getValue(META) + 1

    override fun createBlockState() = BlockStateContainer(this, META)

    override fun getMetaFromState(state: IBlockState) = state.getValue(META)

    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(META, meta)

    override fun damageDropped(state: IBlockState) = getMetaFromState(state)

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (i in 0..9) {
            items.add(ItemStack(this, 1, i))
        }
    }

    companion object {
        val META = PropertyInteger.create("meta", 0, 9)
    }
}
