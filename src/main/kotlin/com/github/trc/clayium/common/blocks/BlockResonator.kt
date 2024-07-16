package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.block.IResonatingBlock
import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.ITier
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("OVERRIDE_DEPRECATION")
class BlockResonator : Block(Material.IRON), IResonatingBlock, ITieredBlock {
    init {
        setSoundType(SoundType.METAL)
        setHardness(2.0f)
        setResistance(5.0f)
        setHarvestLevel("pickaxe", 0)
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (i in 0..3) {
            items.add(ItemStack(this, 1, i))
        }
    }

    override fun createBlockState() = BlockStateContainer(this, META)
    override fun getMetaFromState(state: IBlockState) = state.getValue(META)
    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(META, meta)
    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand)
        = getStateFromMeta(meta)
    override fun damageDropped(state: IBlockState) = getMetaFromState(state)

    override fun getResonance(state: IBlockState): Double {
        return RESONANCE[state.getValue(META)]
    }

    override fun getTier(stack: ItemStack): ITier {
        return ClayTiers.entries[stack.metadata + MIN_TIER]
    }

    override fun getTier(world: IBlockAccess, pos: BlockPos): ITier {
        return ClayTiers.entries[world.getBlockState(pos).getValue(META) + MIN_TIER]
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18n.format("gui.${CValues.MOD_ID}.resonance", RESONANCE[stack.metadata]))
    }

    companion object {
        const val MIN_TIER = 10
        val RESONANCE = doubleArrayOf(1.08, 1.1, 2.0, 20.0)
        val META = PropertyInteger.create("meta", 0, RESONANCE.size - 1)
    }
}