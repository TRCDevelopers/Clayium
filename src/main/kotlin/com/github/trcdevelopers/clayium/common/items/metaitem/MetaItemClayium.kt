package com.github.trcdevelopers.clayium.common.items.metaitem

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.items.ItemClayium
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemBehavior
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemColorHandler
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemComponent
import com.github.trcdevelopers.clayium.common.items.metaitem.component.ISubItemHandler
import com.github.trcdevelopers.clayium.common.items.metaitem.component.TooltipBehavior
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.IRarity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary

abstract class MetaItemClayium(name: String) : ItemClayium(name) {

    init {
        hasSubtypes = true

        @Suppress("LeakingThis")
        _metaItems.add(this)
    }

    protected val metaValueItems = mutableMapOf<Short, MetaValueItem>()

    protected fun addItem(meta: Short, name: String, itemModifier: MetaValueItem.() -> Unit = {}): MetaValueItem {
        val item = MetaValueItem(meta, name)
        item.itemModifier()
        this.metaValueItems[meta] = item
        return item
    }

    private fun getItem(meta: Short): MetaValueItem? {
        return this.metaValueItems[meta]
    }

    @SideOnly(Side.CLIENT)
    fun registerColorHandler() {
        Minecraft.getMinecraft().itemColors.registerItemColorHandler({ stack, tintIndex ->
            getItem(stack.itemDamage.toShort())?.colorHandler?.getColor(stack, tintIndex) ?: 0xFFFFFF
        }, this)
    }

    @SideOnly(Side.CLIENT)
    open fun registerModels() {
        for (item in this.metaValueItems.values) {
            ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), ModelResourceLocation("${Clayium.MOD_ID}:${item.name}", "inventory"))
        }
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return getItem(stack.itemDamage.toShort())?.translationKey ?: "item.invalid"
    }

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return getItem(stack.itemDamage.toShort())?.rarity ?: super.getForgeRarity(stack)
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (!isInCreativeTab(tab)) return
        for (meta in this.metaValueItems.keys.map(Short::toInt)) {
            items.add(ItemStack(this, 1, meta))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val item = getItem(stack.itemDamage.toShort()) ?: return

        for (behavior in item.behaviors) {
            behavior.addInformation(stack, worldIn, tooltip, flagIn)
        }
    }

    companion object {
        private val _metaItems = mutableListOf<MetaItemClayium>()
        val META_ITEMS: List<MetaItemClayium> get() = _metaItems
    }

    open inner class MetaValueItem(
        val meta: Short,
        val name: String,
    ) {

        val stackForm = ItemStack(this@MetaItemClayium, 1, meta.toInt())

        val translationKey = "item.${Clayium.MOD_ID}.$name"
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

        /**
         * sets the rarity and adds a tooltip by tier.
         * if -1 is passed, it mutates nothing
         */
        fun tier(tier: Int): MetaValueItem {
            if (tier == -1) return this
            rarity = when (tier) {
                4, 5, 6, 7 -> EnumRarity.UNCOMMON
                8, 9, 10, 11 -> EnumRarity.RARE
                12, 13, 14, 15 -> EnumRarity.EPIC
                else -> EnumRarity.COMMON
            }
            addComponent(TooltipBehavior { it.add("§rTier $tier") })
            return this
        }

        fun oreDict(name: String): MetaValueItem {
            OreDictionary.registerOre(name, stackForm)
            return this
        }

    }
}
