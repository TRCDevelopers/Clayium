package com.github.trcdevelopers.clayium.common

import com.cleanroommc.modularui.factory.GuiManager
import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.block.ItemBlockMachine
import com.github.trcdevelopers.clayium.api.gui.MetaTileEntityGuiFactory
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.blocks.clay.ItemBlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.clay.ItemBlockEnergizedClay
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.TileClayWorkTable
import com.github.trcdevelopers.clayium.common.items.ClayiumItems
import com.github.trcdevelopers.clayium.common.items.ItemBlockTiered
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaPrefixItem
import com.github.trcdevelopers.clayium.common.metatileentity.MetaTileEntities
import com.github.trcdevelopers.clayium.common.recipe.loader.CRecipeLoader
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.worldgen.ClayOreGenerator
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry

open class CommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(Clayium.proxy)
        this.registerTileEntities()
        GameRegistry.registerWorldGenerator(ClayOreGenerator(), 0)
        NetworkRegistry.INSTANCE.registerGuiHandler(Clayium.INSTANCE, GuiHandler)

        MetaTileEntities.init()

        GuiManager.registerFactory(MetaTileEntityGuiFactory)
    }

    open fun init(event: FMLInitializationEvent) {
    }

    open fun postInit(event: FMLPostInitializationEvent) {
        CRecipeLoader.load()
    }

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        val registry: IForgeRegistry<Block> = event.registry

        registerBlock(registry, ClayiumBlocks.CLAY_WORK_TABLE)

        registerBlock(registry, ClayiumBlocks.COMPRESSED_CLAY)
        registerBlock(registry, ClayiumBlocks.ENERGIZED_CLAY)

        registerBlock(registry, ClayiumBlocks.CLAY_ORE)
        registerBlock(registry, ClayiumBlocks.DENSE_CLAY_ORE)
        registerBlock(registry, ClayiumBlocks.LARGE_DENSE_CLAY_ORE)

        registerBlock(registry, ClayiumApi.BLOCK_MACHINE)
    }

    open fun registerBlock(registry: IForgeRegistry<Block>, block: Block) {
        registry.register(block)
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        val registry = event.registry

        registry.register(MetaItemClayParts)
        for (orePrefix in OrePrefix.entries) {
            val metaPrefixItem = MetaPrefixItem.create("meta_${orePrefix.snake}", orePrefix)
            registry.register(metaPrefixItem)
            metaPrefixItem.registerSubItems()
        }

        registerItem(registry, ClayiumItems.CLAY_ROLLING_PIN)
        registerItem(registry, ClayiumItems.CLAY_SLICER)
        registerItem(registry, ClayiumItems.CLAY_SPATULA)
        registerItem(registry, ClayiumItems.CLAY_WRENCH)
        registerItem(registry, ClayiumItems.CLAY_IO_CONFIGURATOR)
        registerItem(registry, ClayiumItems.CLAY_PIPING_TOOL)

        registerItem(registry, ClayiumItems.CLAY_PICKAXE)
        registerItem(registry, ClayiumItems.CLAY_SHOVEL)

        registry.register(createItemBlock(ClayiumBlocks.CLAY_WORK_TABLE, ::ItemBlockTiered))

        registry.register(createItemBlock(ClayiumBlocks.COMPRESSED_CLAY, ::ItemBlockCompressedClay))
        registry.register(createItemBlock(ClayiumBlocks.ENERGIZED_CLAY, ::ItemBlockEnergizedClay))

        registry.register(createItemBlock(ClayiumBlocks.CLAY_ORE, ::ItemBlock))
        registry.register(createItemBlock(ClayiumBlocks.DENSE_CLAY_ORE, ::ItemBlock))
        registry.register(createItemBlock(ClayiumBlocks.LARGE_DENSE_CLAY_ORE, ::ItemBlock))

        registry.register(ClayiumApi.ITEM_BLOCK_MACHINE)
    }

    open fun registerItem(registry: IForgeRegistry<Item>, item: Item) {
        registry.register(item)
    }

    private fun <T: Block> createItemBlock(block: T, producer: (T) -> ItemBlock): ItemBlock {
        return producer(block).apply {
            registryName = block.registryName ?: throw IllegalArgumentException("Block ${block.translationKey} has no registry name")
        }
    }

    open fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileClayWorkTable::class.java, ResourceLocation(Clayium.MOD_ID, "clayWorkTable"))
        GameRegistry.registerTileEntity(MetaTileEntityHolder::class.java, ResourceLocation(Clayium.MOD_ID, "metaTileEntityHolder"))
    }
}
