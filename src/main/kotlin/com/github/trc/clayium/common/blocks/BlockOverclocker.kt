package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.block.IOverclockerBlock
import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.block.VariantBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class BlockOverclocker :
    VariantBlock<BlockCaReactorCoil.BlockType>(Material.IRON), IOverclockerBlock, ITieredBlock {
    init {
        setHardness(2.0f)
        setResistance(2.0f)
        soundType = SoundType.METAL
    }

    override fun getOverclockFactor(world: IBlockAccess, pos: BlockPos) =
        getOcFactor(getEnum(world.getBlockState(pos)))

    private fun getOcFactor(type: BlockCaReactorCoil.BlockType): Double {
        return when (type) {
            BlockCaReactorCoil.BlockType.ANTIMATTER -> 1.5
            BlockCaReactorCoil.BlockType.PURE_ANTIMATTER -> 2.3
            BlockCaReactorCoil.BlockType.OEC -> 3.5
            BlockCaReactorCoil.BlockType.OPA -> 5.0
        }
    }

    override fun getTier(stack: ItemStack) = getEnum(stack).tier

    override fun getTier(world: IBlockAccess, pos: BlockPos) =
        getEnum(world.getBlockState(pos)).tier

    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String>,
        flagIn: ITooltipFlag
    ) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        tooltip.add(I18n.format("tile.clayium.overclocker.factor", getOcFactor(getEnum(stack))))
    }
}
