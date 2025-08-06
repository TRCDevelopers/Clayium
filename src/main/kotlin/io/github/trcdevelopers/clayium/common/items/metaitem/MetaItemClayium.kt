package io.github.trcdevelopers.clayium.common.items.metaitem

import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMaterial
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.unification.stack.UnificationEntry
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.items.ItemClayium
import io.github.trcdevelopers.clayium.common.items.metaitem.component.IItemBehavior
import io.github.trcdevelopers.clayium.common.items.metaitem.component.IItemCapabilityProvider
import io.github.trcdevelopers.clayium.common.items.metaitem.component.IItemColorHandler
import io.github.trcdevelopers.clayium.common.items.metaitem.component.IItemComponent
import io.github.trcdevelopers.clayium.common.items.metaitem.component.ISubItemHandler
import io.github.trcdevelopers.clayium.common.items.metaitem.component.TooltipBehavior
import io.github.trcdevelopers.clayium.common.util.UtilLocale
import it.unimi.dsi.fastutil.shorts.Short2ObjectAVLTreeMap
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.IRarity
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

abstract class MetaItemClayium(name: String) : ItemClayium(name) {

    init {
        hasSubtypes = true

        @Suppress("LeakingThis")
        _metaItems.add(this)
    }

    @Deprecated("Use getTranslationKey(stack: ItemStack) whenever possible. This getter returns same key for all subItems.", ReplaceWith("getTranslationKey(stack)"))
    override fun getTranslationKey(): String {
        return super.getTranslationKey()
    }

    protected val metaValueItems = mutableMapOf<Short, MetaValueItem>()
    protected val metaOreDicts = Short2ObjectAVLTreeMap<String>()

    protected fun addItem(meta: Short, name: String, itemModifier: MetaValueItem.() -> Unit = {}): MetaValueItem {
        val item = MetaValueItem(meta, name)
        item.itemModifier()
        this.metaValueItems[meta] = item
        return item
    }

    private fun getItem(meta: Short) = this.metaValueItems[meta]
    private fun getItem(stack: ItemStack) = getItem(stack.itemDamage.toShort())

    @SideOnly(Side.CLIENT)
    fun registerColorHandler(e: ColorHandlerEvent.Item) {
        e.itemColors.registerItemColorHandler({ stack, tintIndex ->
            getItem(stack.itemDamage.toShort())?.colorHandler?.getColor(stack, tintIndex) ?: 0xFFFFFF
        }, this)
    }

    @SideOnly(Side.CLIENT)
    open fun registerModels() {
        for (item in this.metaValueItems.values) {
            ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), ModelResourceLocation(clayiumId(item.name), "inventory"))
        }
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return getItem(stack)?.capabilityProvider
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

    open fun registerOreDicts() {
        metaOreDicts.forEach { (meta, oreDict) ->
            OreDictUnifier.registerOre(ItemStack(this, 1, meta.toInt()), oreDict)
        }
        metaOreDicts.clear()
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val item = getItem(stack.itemDamage.toShort()) ?: return

        for (behavior in item.behaviors) {
            behavior.addInformation(stack, worldIn, tooltip, flagIn)
        }
    }

    open inner class MetaValueItem(
        val meta: Short,
        val name: String,
    ) {
        val translationKey = "item.$MOD_ID.$name"
        val behaviors = mutableListOf<IItemBehavior>()
        var colorHandler: IItemColorHandler? = null
        var rarity: IRarity = EnumRarity.COMMON
        var capabilityProvider: IItemCapabilityProvider? = null

        fun getStackForm(count: Int = 1): ItemStack = ItemStack(this@MetaItemClayium, count, meta.toInt())

        fun addComponent(component: IItemComponent): MetaValueItem {
            when (component) {
                is ISubItemHandler -> {}
                is IItemBehavior -> behaviors.add(component)
                is IItemColorHandler -> colorHandler = component
                is IItemCapabilityProvider -> capabilityProvider = component
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
            addComponent(TooltipBehavior { it.add(1, "§rTier $tier") })
            return this
        }

        fun oreDict(name: String): MetaValueItem {
            metaOreDicts.put(meta, name)
            return this
        }

        fun oreDict(orePrefix: OrePrefix, material: CMaterial): MetaValueItem {
            metaOreDicts.put(meta, UnificationEntry(orePrefix, material).toString())
            return this
        }

        fun tooltip(translationKey: String) = apply {
            addComponent(TooltipBehavior { UtilLocale.formatTooltips(it, translationKey) })
        }
    }

    companion object {
        private val _metaItems = mutableListOf<MetaItemClayium>()
        val META_ITEMS: List<MetaItemClayium> get() = _metaItems

        @SideOnly(Side.CLIENT)
        fun registerModels() {
            for (item in META_ITEMS) {
                item.registerModels()
            }
        }

        @SideOnly(Side.CLIENT)
        fun registerColors(e: ColorHandlerEvent.Item) {
            for (item in META_ITEMS) {
                item.registerColorHandler(e)
            }
        }
    }
}
