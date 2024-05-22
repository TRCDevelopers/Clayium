package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.block.ITieredBlock
import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class BlockMachineHull : Block(Material.IRON), ITieredBlock {

    init {
        creativeTab = Clayium.creativeTab
        setHardness(2.0f)
        setResistance(5.0f)
        setHarvestLevel("pickaxe", 0)
        setSoundType(SoundType.METAL)
        defaultState = defaultState.withProperty(META, 1)
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (i in 1..13) {
            items.add(ItemStack(this, 1, i))
        }
    }

    override fun createBlockState() = BlockStateContainer.Builder(this).add(META).build()
    override fun getMetaFromState(state: IBlockState) = state.getValue(META)
    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(META, meta)
    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand)
        = getStateFromMeta(meta)
    override fun damageDropped(state: IBlockState) = getMetaFromState(state)

    override fun getTier(stack: ItemStack): ITier  {
        val tier = stack.metadata.coerceIn(1, 13)
        return ClayTiers.entries[tier]
    }

    override fun getTier(world: IBlockAccess, pos: BlockPos): ITier {
        val tier = world.getBlockState(pos).getValue(META).coerceIn(1, 13)
        return ClayTiers.entries[tier]
    }

    companion object {
        val META = PropertyInteger.create("meta", 1, 13)
    }
}