package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.capability.IConfigurationTool
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.ClayiumMod
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterBlockMetadata
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterDamageValue
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterDisplayName
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterModID
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterOreDictionary
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterRegistryName
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterUnlocalizedName
import io.github.trcdevelopers.clayium.common.constants.ToolConstants
import io.github.trcdevelopers.clayium.common.creativetab.ClayiumCTabs
import io.github.trcdevelopers.clayium.common.items.filter.ItemFilterDuplicator
import io.github.trcdevelopers.clayium.common.items.filter.ItemFuzzyItemFilter
import io.github.trcdevelopers.clayium.common.items.filter.ItemSimpleItemFilter
import io.github.trcdevelopers.clayium.common.items.filter.ItemStringItemFilter
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayGadget
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaPrefixItem
import io.github.trcdevelopers.clayium.common.util.ToolClasses
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.registries.IForgeRegistry

object ClayiumItems {

    //region Tools
    val CLAY_ROLLING_PIN = createItem("clay_rolling_pin", ItemClayConfigTool(maxDamage = 60, type = IConfigurationTool.ToolType.INSERTION))
    val CLAY_SLICER = createItem("clay_slicer", ItemClayConfigTool(maxDamage = 60, type = IConfigurationTool.ToolType.EXTRACTION))
    val CLAY_SPATULA = createItem("clay_spatula", ItemClayConfigTool(maxDamage = 36, type = IConfigurationTool.ToolType.PIPING))

    val CLAY_WRENCH = createItem("clay_wrench", ItemClayConfigTool(maxDamage = 0, type = IConfigurationTool.ToolType.ROTATION))
    val CLAY_IO_CONFIGURATOR = createItem("clay_io_configurator", ItemClayConfigTool(maxDamage = 0, type = IConfigurationTool.ToolType.INSERTION, typeWhenSneak = IConfigurationTool.ToolType.EXTRACTION))
    val CLAY_PIPING_TOOL = createItem("clay_piping_tool", ItemClayConfigTool(maxDamage = 0, type = IConfigurationTool.ToolType.PIPING, typeWhenSneak = IConfigurationTool.ToolType.ROTATION))

    val MEMORY_CARD = createItem("memory_card", ItemMemoryCard())
    val DIRECTION_MEMORY = createItem("direction_memory", ItemDirectionMemory())
    val SYNCHRONIZER = createItem("synchronizer", ItemSynchronizer())
    //endregion

    val CLAY_PICKAXE = createItem("clay_pickaxe", ItemClayPickaxe())
    val CLAY_SHOVEL = createItem("clay_shovel", ItemClayShovel())

    val CLAY_STEEL_PICKAXE = createItem("clay_steel_pickaxe", ItemClaySteelTool(
        ToolConstants.PICKAXE_ATTACK_DAMAGE_SCALE, ToolConstants.PICKAXE_ATTACK_SPEED_SCALE, ToolClasses.PICKAXE)
    )
    val CLAY_STEEL_SHOVEL = createItem("clay_steel_shovel", ItemClaySteelTool(
        ToolConstants.SHOVEL_ATTACK_DAMAGE_SCALE, ToolConstants.SHOVEL_ATTACK_SPEED_SCALE, ToolClasses.SHOVEL)
    )

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

    val CLAY_GADGET_HOLDER = createItem("clay_gadget_holder", ItemClayGadgetHolder())

    val FLUID_CAPSULE_1MB = createItem("fluid_capsule_1mb", ItemFluidCapsule(1), ClayiumCTabs.fluidCapsules)
    val FLUID_CAPSULE_5MB = createItem("fluid_capsule_5mb", ItemFluidCapsule(5), ClayiumCTabs.fluidCapsules)
    val FLUID_CAPSULE_25MB = createItem("fluid_capsule_25mb", ItemFluidCapsule(25), ClayiumCTabs.fluidCapsules)
    val FLUID_CAPSULE_125MB = createItem("fluid_capsule_125mb", ItemFluidCapsule(125), ClayiumCTabs.fluidCapsules)
    val FLUID_CAPSULE_1000MB = createItem("fluid_capsule_1000mb", ItemFluidCapsule(1000, addSubItemsToCreativeTab = true), ClayiumCTabs.fluidCapsules)

    val DUMMY: Item = Item().setRegistryName(clayiumId("dummy"))

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

    private fun <T: Item> createItem(name: String, item: T, creativeTab: CreativeTabs): T {
        return item.apply {
            setCreativeTab(creativeTab)
            registryName = clayiumId(name)
            setTranslationKey("${MOD_ID}.$name")
        }
    }

    fun registerItems(registry: IForgeRegistry<Item>) {
        val proxy = ClayiumMod.proxy

        for (orePrefix in OrePrefix.metaItemPrefixes) {
            val metaPrefixItem = MetaPrefixItem.create("meta_${orePrefix.snake}", orePrefix)
            metaPrefixItem.registerSubItems()
            proxy.registerItem(registry, metaPrefixItem)
        }

        proxy.registerItem(registry, MetaItemClayGadget)
        proxy.registerItem(registry, MetaItemClayParts)

        proxy.registerItem(registry, CLAY_GADGET_HOLDER)

        proxy.registerItem(registry, CLAY_ROLLING_PIN)
        proxy.registerItem(registry, CLAY_SLICER)
        proxy.registerItem(registry, CLAY_SPATULA)
        proxy.registerItem(registry, CLAY_WRENCH)
        proxy.registerItem(registry, CLAY_IO_CONFIGURATOR)
        proxy.registerItem(registry, CLAY_PIPING_TOOL)

        proxy.registerItem(registry, CLAY_PICKAXE)
        proxy.registerItem(registry, CLAY_SHOVEL)
        proxy.registerItem(registry, CLAY_STEEL_PICKAXE)
        proxy.registerItem(registry, CLAY_STEEL_SHOVEL)

        proxy.registerItem(registry, MEMORY_CARD)
        proxy.registerItem(registry, DIRECTION_MEMORY)
        proxy.registerItem(registry, SYNCHRONIZER)

        proxy.registerItem(registry, SIMPLE_ITEM_FILTER)
        proxy.registerItem(registry, FUZZY_ITEM_FILTER)
        proxy.registerItem(registry, ORE_DICT_ITEM_FILTER)
        proxy.registerItem(registry, REGISTRY_NAME_ITEM_FILTER)
        proxy.registerItem(registry, DISPLAY_NAME_ITEM_FILTER)
        proxy.registerItem(registry, UNLOCALIZED_NAME_ITEM_FILTER)
        proxy.registerItem(registry, MOD_ID_ITEM_FILTER)
        proxy.registerItem(registry, DAMAGE_VALUE_ITEM_FILTER)
        proxy.registerItem(registry, BLOCK_METADATA_ITEM_FILTER)
        proxy.registerItem(registry, ITEM_FILTER_DUPLICATOR)

        proxy.registerItem(registry, FLUID_CAPSULE_1MB)
        proxy.registerItem(registry, FLUID_CAPSULE_5MB)
        proxy.registerItem(registry, FLUID_CAPSULE_25MB)
        proxy.registerItem(registry, FLUID_CAPSULE_125MB)
        proxy.registerItem(registry, FLUID_CAPSULE_1000MB)

        proxy.registerItem(registry, DUMMY)
    }
}
