package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.block.IResonatingBlock
import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.block.VariantBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("OVERRIDE_DEPRECATION")
class BlockResonator : VariantBlock<BlockCaReactorCoil.BlockType>(Material.IRON), IResonatingBlock, ITieredBlock {
    init {
        setSoundType(SoundType.METAL)
        setHardness(2.0f)
        setResistance(5.0f)
        setHarvestLevel("pickaxe", 0)
    }

    override fun getResonance(state: IBlockState) = RESONANCE[getEnum(state).ordinal]
    override fun getTier(stack: ItemStack) = getEnum(stack).tier
    override fun getTier(world: IBlockAccess, pos: BlockPos) = getEnum(world.getBlockState(pos)).tier

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18n.format("gui.${CValues.MOD_ID}.resonance", RESONANCE[stack.metadata]))
    }

    companion object {
        val RESONANCE = doubleArrayOf(1.08, 1.1, 2.0, 20.0)
    }
}