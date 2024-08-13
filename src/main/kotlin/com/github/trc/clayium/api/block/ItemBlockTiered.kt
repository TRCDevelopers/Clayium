package com.github.trc.clayium.api.block

import com.github.trc.clayium.api.item.ITieredItem
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.util.UtilLocale
import net.minecraft.block.Block
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBlockTiered<T>(
    private val tieredBlock: T,
    hasSubTypes: Boolean = true,
) : ItemBlock(tieredBlock), ITieredItem
        where T : Block, T : ITieredBlock {
    init {
        hasSubtypes = hasSubTypes
    }
    override fun getMetadata(damage: Int) = if (hasSubtypes) damage else 0
    override fun getForgeRarity(stack: ItemStack) = tieredBlock.getTier(stack).rarity
    override fun getTier(stack: ItemStack) = tieredBlock.getTier(stack)

    @SideOnly(Side.CLIENT)
    override fun getItemStackDisplayName(stack: ItemStack): String {
        val tieredKey = "$translationKey.${this.getTier(stack).lowerName}"
        // first search for tiered
        return if (I18n.hasKey(tieredKey)) {
            return I18n.format(tieredKey)
        }
        // then search for tier-less
        else if (I18n.hasKey(translationKey)) {
            return I18n.format(translationKey, I18n.format(this.getTier(stack).prefixTranslationKey))
        }
        // fallback to super
        else {
            return super.getItemStackDisplayName(stack)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(ITier.tierNumericTooltip(this.getTier(stack)))
        UtilLocale.formatTooltips(tooltip, "$translationKey.${this.getTier(stack).lowerName}.tooltip")
        UtilLocale.formatTooltips(tooltip, "$translationKey.tooltip")
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    companion object {
        fun <T> noSubTypes(tieredBlock: T) where T : Block, T : ITieredBlock = ItemBlockTiered(tieredBlock, false)
    }
}