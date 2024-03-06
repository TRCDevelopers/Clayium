package com.github.trcdevelopers.clayium.common.items

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.Clayium.Companion.MOD_ID
import com.github.trcdevelopers.clayium.common.annotation.CItem
import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks.allBlocks
import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine
import com.github.trcdevelopers.clayium.common.blocks.machine.ItemBlockMachine
import com.google.common.reflect.ClassPath
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.IRarity
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Collections

object ClayiumItems {

    @SideOnly(Side.CLIENT)
    private lateinit var itemColors: MutableMap<Item, IItemColor>
    private val items: MutableMap<String, Item> = HashMap()
    @JvmStatic
    fun getItem(registryName: String): Item? {
        return items[registryName]
    }

    val allItems: Map<String, Item>
        get() = Collections.unmodifiableMap(items)

    fun registerItems(event: RegistryEvent.Register<Item>, side: Side) {
        if (side.isClient) {
            itemColors = HashMap()
        }
        //参考 https://blog1.mammb.com/entry/2015/03/31/001620
        val classLoader = Thread.currentThread().contextClassLoader
        ClassPath.from(classLoader)
            .getTopLevelClassesRecursive("com.github.trcdevelopers.clayium.common.items")
            .map(ClassPath.ClassInfo::load)
            .forEach { clazz ->
                val cItem = clazz.getAnnotation(CItem::class.java) ?: return@forEach
                val item = clazz.newInstance() as Item
                val registryName = cItem.registryName

                item.creativeTab = Clayium.creativeTab
                item.registryName = ResourceLocation(MOD_ID, registryName)
                item.translationKey = "$MOD_ID.$registryName"
                event.registry.register(item)
                items[registryName] = item
                if (side.isClient) {
                    ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(ResourceLocation(MOD_ID, registryName), "inventory"))
                }
            }
        registerItem(Item().setMaxDamage(100).setMaxStackSize(1), "clay_spatula", side, event)
        registerItem(Item().setMaxDamage(100).setMaxStackSize(1), "clay_rolling_pin", side, event)
        registerItem(Item().setMaxDamage(100).setMaxStackSize(1), "clay_slicer", side, event)
        registerItem(Item().setMaxStackSize(1), "clay_piping_tool", side, event)
        registerItem(Item().setMaxStackSize(1), "clay_io_configurator", side, event)
        allBlocks.forEach { (registryName: String, block: Block) ->
            if (block is BlockMachine) {
                event.registry.register(ItemBlockMachine(block))
                (Item.getItemFromBlock(block) as ItemBlockMachine).registerModels()
                return@forEach
            }
            event.registry.register(ItemBlock(block).setRegistryName(registryName))
            if (side.isClient) {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, ModelResourceLocation(ResourceLocation(MOD_ID, registryName), "inventory"))
            }
        }
    }

    private fun registerItem(item: Item, registryName: String, side: Side, event: RegistryEvent.Register<Item>) {
        item.creativeTab = Clayium.creativeTab
        item.registryName = ResourceLocation(MOD_ID, registryName)
        item.translationKey = "$MOD_ID.$registryName"
        event.registry.register(item)
        items[registryName] = item
        if (side.isClient) {
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(ResourceLocation(MOD_ID, registryName), "inventory"))
        }
    }

    @SideOnly(Side.CLIENT)
    fun registerItemColors() {
        itemColors.forEach { (item, itemColor) -> Minecraft.getMinecraft().itemColors.registerItemColorHandler(itemColor, item) }
    }

    fun getRarity(tier: Int): IRarity {
        return when (tier) {
            4, 5, 6, 7 -> EnumRarity.UNCOMMON
            8, 9, 10, 11 -> EnumRarity.RARE
            12, 13, 14, 15 -> EnumRarity.EPIC
            else -> EnumRarity.COMMON
        }
    }

    @JvmStatic
    @ObjectHolder("$MOD_ID:clay_spatula")
    lateinit var CLAY_SPATULA: Item
        private set
    @JvmStatic
    @ObjectHolder("$MOD_ID:clay_rolling_pin")
    lateinit var CLAY_ROLLING_PIN: Item
        private set
    @ObjectHolder("$MOD_ID:clay_slicer")
    lateinit var CLAY_SLICER: Item
        private set
    @ObjectHolder("$MOD_ID:clay_piping_tool")
    lateinit var CLAY_PIPING_TOOL: Item
        private set
    @ObjectHolder("$MOD_ID:clay_io_configurator")
    lateinit var CLAY_IO_CONFIGURATOR: Item
        private set
}
