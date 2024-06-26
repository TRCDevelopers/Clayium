package com.github.trcdevelopers.clayium.common.items

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.items.filter.ItemSimpleItemFilter
import net.minecraft.item.Item

object ClayiumItems {

    //region Tools
    val CLAY_ROLLING_PIN = createItem("clay_rolling_pin", ItemClayConfigTool(maxDamage = 60, type = ItemClayConfigTool.ToolType.INSERTION))
    val CLAY_SLICER = createItem("clay_slicer", ItemClayConfigTool(maxDamage = 60, type = ItemClayConfigTool.ToolType.EXTRACTION))
    val CLAY_SPATULA = createItem("clay_spatula", ItemClayConfigTool(maxDamage = 36, type = ItemClayConfigTool.ToolType.PIPING))

    val CLAY_WRENCH = createItem("clay_wrench", ItemClayConfigTool(maxDamage = 0, type = ItemClayConfigTool.ToolType.ROTATION))
    val CLAY_IO_CONFIGURATOR = createItem("clay_io_configurator", ItemClayConfigTool(maxDamage = 0, type = ItemClayConfigTool.ToolType.INSERTION, typeWhenSneak = ItemClayConfigTool.ToolType.EXTRACTION))
    val CLAY_PIPING_TOOL = createItem("clay_piping_tool", ItemClayConfigTool(maxDamage = 0, type = ItemClayConfigTool.ToolType.PIPING, typeWhenSneak = ItemClayConfigTool.ToolType.ROTATION))

    val SYNCHRONIZER = createItem("synchronizer", ItemSynchronizer())
    //endregion

    val CLAY_PICKAXE = createItem("clay_pickaxe", ItemClayPickaxe())
    val CLAY_SHOVEL = createItem("clay_shovel", ItemClayShovel())

    val simpleItemFilter = createItem("simple_item_filter", ItemSimpleItemFilter())

    private fun <T: Item> createItem(name: String, item: T): T {
        return item.apply {
            setCreativeTab(Clayium.creativeTab)
            setRegistryName(Clayium.MOD_ID, name)
            setTranslationKey("${Clayium.MOD_ID}.$name")
        }
    }
}
