package com.github.trcdevelopers.clayium.common.items.metaitem

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.items.ItemClayium
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemBehavior
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemColorHandler
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemComponent
import com.github.trcdevelopers.clayium.common.items.metaitem.component.ISubItemHandler
import com.github.trcdevelopers.clayium.common.items.metaitem.component.TooltipBehavior
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.IRarity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

abstract class MetaItemClayium(name: String) : ItemClayium(name) {

    private val metaValueItems = mutableMapOf<Short, MetaValueItem>()

    /**
     * @param translationKey "item.$MOD_ID." is added automatically.
     */
    fun addItem(
        meta: Short,
        translationKey: String,
        model: ResourceLocation = ResourceLocation(Clayium.MOD_ID, "item/$translationKey"),
    ): MetaValueItem {
        val item = MetaValueItem(meta, "item.${Clayium.MOD_ID}.$translationKey")
        metaValueItems[meta] = item
        return item
    }

    private fun getItem(meta: Short): MetaValueItem? {
        return metaValueItems[meta]
    }


    @SideOnly(Side.CLIENT)
    fun registerColorHandler() {
        Minecraft.getMinecraft().itemColors.registerItemColorHandler({ stack, tintIndex ->
            getItem(stack.itemDamage.toShort())?.colorHandler?.getColor(stack, tintIndex) ?: 0xFFFFFF
        }, this)
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return getItem(stack.itemDamage.toShort())?.translationKey ?: "item.invalid"
    }

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return getItem(stack.itemDamage.toShort())?.rarity ?: super.getForgeRarity(stack)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val item = getItem(stack.itemDamage.toShort()) ?: return

        for (behavior in item.behaviors) {
            behavior.addInformation(stack, worldIn, tooltip, flagIn)
        }
    }

    inner class MetaValueItem(
        val meta: Short,
        val translationKey: String,
    ) {
        val behaviors = mutableListOf<IItemBehavior>()

        var colorHandler: IItemColorHandler? = null
        var rarity: IRarity = EnumRarity.COMMON

        fun addComponent(component: IItemComponent): MetaValueItem {
            when (component) {
                is ISubItemHandler -> {}
                is IItemBehavior -> behaviors.add(component)
                is IItemColorHandler -> colorHandler = component
            }
            return this
        }

        fun tier(tier: Int): MetaValueItem {
            rarity = when (tier) {
                4, 5, 6, 7 -> EnumRarity.UNCOMMON
                8, 9, 10, 11 -> EnumRarity.RARE
                12, 13, 14, 15 -> EnumRarity.EPIC
                else -> EnumRarity.COMMON
            }
            addComponent(TooltipBehavior { it.add("§rTier $tier") })
            return this
        }
    }
}
