package com.github.trc.clayium.api.block

import com.github.trc.clayium.common.util.UtilLocale
import net.minecraft.block.Block
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemBlockDamaged<T>(
    tieredBlock: T,
) : ItemBlockTiered<T>(tieredBlock) where T : Block, T : ITieredBlock {
    @SideOnly(Side.CLIENT)
    override fun getItemStackDisplayName(stack: ItemStack): String {
        return I18n.format("$translationKey.${stack.metadata}")
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String>,
        flagIn: ITooltipFlag
    ) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        UtilLocale.formatTooltips(tooltip, "$translationKey.${stack.metadata}.tooltip")
    }
}
