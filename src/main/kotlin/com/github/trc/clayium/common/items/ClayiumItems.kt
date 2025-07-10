package com.github.trc.clayium.common.items

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.capability.IConfigurationTool
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.capability.impl.ItemFilterBlockMetadata
import com.github.trc.clayium.common.capability.impl.ItemFilterDamageValue
import com.github.trc.clayium.common.capability.impl.ItemFilterDisplayName
import com.github.trc.clayium.common.capability.impl.ItemFilterModID
import com.github.trc.clayium.common.capability.impl.ItemFilterOreDictionary
import com.github.trc.clayium.common.capability.impl.ItemFilterRegistryName
import com.github.trc.clayium.common.capability.impl.ItemFilterUnlocalizedName
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import com.github.trc.clayium.common.items.filter.ItemFilterDuplicator
import com.github.trc.clayium.common.items.filter.ItemFuzzyItemFilter
import com.github.trc.clayium.common.items.filter.ItemSimpleItemFilter
import com.github.trc.clayium.common.items.filter.ItemStringItemFilter
import com.github.trc.clayium.common.items.metaitem.MetaItemClayium
import net.minecraft.item.Item

object ClayiumItems {

    //region Tools
    val CLAY_ROLLING_PIN = createItem("clay_rolling_pin", ItemClayConfigTool(maxDamage = 60, type = IConfigurationTool.ToolType.INSERTION))
    val CLAY_SLICER = createItem("clay_slicer", ItemClayConfigTool(maxDamage = 60, type = IConfigurationTool.ToolType.EXTRACTION))
    val CLAY_SPATULA = createItem("clay_spatula", ItemClayConfigTool(maxDamage = 36, type = IConfigurationTool.ToolType.PIPING))

    val CLAY_WRENCH = createItem("clay_wrench", ItemClayConfigTool(maxDamage = 0, type = IConfigurationTool.ToolType.ROTATION))
    val CLAY_IO_CONFIGURATOR = createItem("clay_io_configurator", ItemClayConfigTool(maxDamage = 0, type = IConfigurationTool.ToolType.INSERTION, typeWhenSneak = IConfigurationTool.ToolType.EXTRACTION))
    val CLAY_PIPING_TOOL = createItem("clay_piping_tool", ItemClayConfigTool(maxDamage = 0, type = IConfigurationTool.ToolType.PIPING, typeWhenSneak = IConfigurationTool.ToolType.ROTATION))

    val MEMORY_CARD = createItem("memory_card", ItemMemoryCard())
    val SYNCHRONIZER = createItem("synchronizer", ItemSynchronizer())
    //endregion

    val CLAY_PICKAXE = createItem("clay_pickaxe", ItemClayPickaxe())
    val CLAY_SHOVEL = createItem("clay_shovel", ItemClayShovel())

    val CLAY_STEEL_PICKAXE = createItem("clay_steel_pickaxe", ItemClaySteelPickaxe())

    val SIMPLE_ITEM_FILTER = createItem("simple_item_filter", ItemSimpleItemFilter())
    val FUZZY_ITEM_FILTER = createItem("item_filter_fuzzy", ItemFuzzyItemFilter())
    val ORE_DICT_ITEM_FILTER = createItem("item_filter_ore_dict", ItemStringItemFilter(::ItemFilterOreDictionary, "Example: ore.*"))
    val REGISTRY_NAME_ITEM_FILTER = createItem("item_filter_registry_name", ItemStringItemFilter(::ItemFilterRegistryName, "Example: minecraft:stone"))
    val DISPLAY_NAME_ITEM_FILTER = createItem("item_filter_display_name", ItemStringItemFilter(::ItemFilterDisplayName))
    val UNLOCALIZED_NAME_ITEM_FILTER = createItem("item_filter_unlocalized_name", ItemStringItemFilter(::ItemFilterUnlocalizedName))
    val MOD_ID_ITEM_FILTER = createItem("item_filter_mod_id", ItemStringItemFilter(::ItemFilterModID, "Example: clayium"))
    val DAMAGE_VALUE_ITEM_FILTER = createItem("item_filter_damage_value", ItemStringItemFilter(::ItemFilterDamageValue))
    val BLOCK_METADATA_ITEM_FILTER = createItem("item_filter_block_metadata", ItemStringItemFilter(::ItemFilterBlockMetadata))
    val ITEM_FILTER_DUPLICATOR = createItem("item_filter_duplicator", ItemFilterDuplicator())

    fun registerOreDicts() {
        for (metaItem in MetaItemClayium.META_ITEMS) {
            metaItem.registerOreDicts()
        }
    }

    private fun <T: Item> createItem(name: String, item: T): T {
        return item.apply {
            setCreativeTab(ClayiumCTabs.main)
            setRegistryName(clayiumId(name))
            setTranslationKey("${MOD_ID}.$name")
        }
    }
}
