package com.github.trc.clayium.common

import com.cleanroommc.modularui.factory.GuiManager
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.block.ItemBlockDamaged
import com.github.trc.clayium.api.block.ItemBlockTiered
import com.github.trc.clayium.api.block.VariantItemBlock
import com.github.trc.clayium.api.capability.SimpleCapabilityManager
import com.github.trc.clayium.api.events.ClayiumMteRegistryEvent
import com.github.trc.clayium.api.gui.MetaTileEntityGuiFactory
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.client.ClientProxy
import com.github.trc.clayium.common.blocks.BlockQuartzCrucible
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.blocks.ItemBlockClayLaserReflector
import com.github.trc.clayium.common.blocks.ItemBlockEnergizedClay
import com.github.trc.clayium.common.blocks.ItemBlockMaterial
import com.github.trc.clayium.common.blocks.TileEntityClayLaserReflector
import com.github.trc.clayium.common.blocks.TileEntityCreativeEnergySource
import com.github.trc.clayium.common.blocks.chunkloader.ChunkLoaderTileEntity
import com.github.trc.clayium.common.blocks.claycraftingtable.TileClayCraftingTable
import com.github.trc.clayium.common.blocks.clayworktable.TileClayWorkTable
import com.github.trc.clayium.common.blocks.marker.TileClayMarker
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import com.github.trc.clayium.common.items.ClayiumItems
import com.github.trc.clayium.common.items.ItemClaySteelPickaxe
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.items.metaitem.MetaPrefixItem
import com.github.trc.clayium.common.loaders.OreDictionaryLoader
import com.github.trc.clayium.common.loaders.recipe.CRecipeLoader
import com.github.trc.clayium.common.metatileentities.MetaTileEntities
import com.github.trc.clayium.common.network.CNetwork
import com.github.trc.clayium.common.pan.factories.CPanRecipeFactory
import com.github.trc.clayium.common.pan.factories.CraftingTablePanRecipeFactory
import com.github.trc.clayium.common.pan.factories.FurnacePanRecipeFactory
import com.github.trc.clayium.common.unification.ClayiumOreDictUnifierImpl
import com.github.trc.clayium.common.util.DebugUtils
import com.github.trc.clayium.common.worldgen.ClayOreGenerator
import com.github.trc.clayium.integration.CModIntegration
import com.github.trc.clayium.integration.gregtech.GTOreDictUnifierAdapter
import com.github.trc.clayium.network.ClayChunkLoaderCallback
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry

open class CommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(ClayiumMod.proxy)
        MinecraftForge.EVENT_BUS.register(ItemClaySteelPickaxe)
        if (CUtils.isDeobfEnvironment) { MinecraftForge.EVENT_BUS.register(DebugUtils::class.java) }

        ClayiumCTabs.init()
        CNetwork.init()

        this.registerTileEntities()
        GameRegistry.registerWorldGenerator(ClayOreGenerator, 0)
        NetworkRegistry.INSTANCE.registerGuiHandler(ClayiumMod, GuiHandler)

        MinecraftForge.EVENT_BUS.post(ClayiumMteRegistryEvent(ClayiumApi.mteManager))
        MetaTileEntities.init()
        CMaterials.init()
        OrePrefix.init()

        GuiManager.registerFactory(MetaTileEntityGuiFactory)

        SimpleCapabilityManager.registerCapabilities()

        ClayiumApi.PAN_RECIPE_FACTORIES.add(CPanRecipeFactory)
        ClayiumApi.PAN_RECIPE_FACTORIES.add(CraftingTablePanRecipeFactory)
        ClayiumApi.PAN_RECIPE_FACTORIES.add(FurnacePanRecipeFactory)

        ForgeChunkManager.setForcedChunkLoadingCallback(ClayiumMod, ClayChunkLoaderCallback)

        if (Mods.GregTech.isModLoaded) {
            OreDictUnifier.injectImpl(GTOreDictUnifierAdapter)
        } else {
            OreDictUnifier.injectImpl(ClayiumOreDictUnifierImpl)
        }
    }

    open fun init(event: FMLInitializationEvent) {
        CModIntegration.init(event)
    }

    open fun postInit(event: FMLPostInitializationEvent) {
        ClientProxy().postInit(event)
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerRecipes(event: RegistryEvent.Register<IRecipe>) {
        OreDictionaryLoader.loadOreDictionaries()
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun registerRecipesLate(event: RegistryEvent.Register<IRecipe>) {
        CRecipeLoader.load()
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        CLog.info("Registering blocks...")
        val registry: IForgeRegistry<Block> = event.registry

        ClayiumBlocks.registerBlocks(event)

        for (block in ClayiumBlocks.ENERGIZED_CLAY_BLOCKS) registry.register(block)
        for (block in ClayiumBlocks.COMPRESSED_CLAY_BLOCKS) registry.register(block)
        for (block in ClayiumBlocks.COMPRESSED_BLOCKS) registry.register(block)
    }

    //todo move to ClayiumBlocks/Items
    @Suppress("unused")
    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        CLog.info("Registering items...")
        val registry = event.registry

        ClayiumBlocks.registerItemBlocks(event)

        //todo: move to somewhere else
        registry.register(MetaItemClayParts)
        for (orePrefix in OrePrefix.metaItemPrefixes) {
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
        registerItem(registry, ClayiumItems.CLAY_STEEL_PICKAXE)

        registerItem(registry, ClayiumItems.MEMORY_CARD)
        registerItem(registry, ClayiumItems.SYNCHRONIZER)
        registerItem(registry, ClayiumItems.simpleItemFilter)

        registry.register(createItemBlock(ClayiumBlocks.CREATIVE_ENERGY_SOURCE, ::ItemBlock))

        registry.register(createItemBlock(ClayiumBlocks.CLAY_CRAFTING_BOARD, ::ItemBlockTiered))
        registry.register(createItemBlock(ClayiumBlocks.CLAY_WORK_TABLE, ::ItemBlockTiered))

        registry.register(createItemBlock(ClayiumBlocks.CLAY_ORE, ::ItemBlock))
        registry.register(createItemBlock(ClayiumBlocks.DENSE_CLAY_ORE, ::ItemBlock))
        registry.register(createItemBlock(ClayiumBlocks.LARGE_DENSE_CLAY_ORE, ::ItemBlock))

        registry.register(createItemBlock(ClayiumBlocks.MACHINE_HULL, ::ItemBlockTiered))
        registry.register(createItemBlock(ClayiumBlocks.RESONATOR, ::ItemBlockTiered))
        registry.register(createItemBlock(ClayiumBlocks.CA_REACTOR_HULL, ::ItemBlockDamaged))
        registry.register(createItemBlock(ClayiumBlocks.CA_REACTOR_COIL, ::ItemBlockTiered))

        registry.register(createItemBlock(ClayiumBlocks.QUARTZ_CRUCIBLE, ItemBlockTiered<*>::noSubTypes))

        registry.register(createItemBlock(ClayiumBlocks.PAN_CABLE, ItemBlockTiered<*>::noSubTypes))

        registry.register(createItemBlock(ClayiumBlocks.CLAY_TREE_LOG, ItemBlockTiered<*>::noSubTypes))
        registry.register(createItemBlock(ClayiumBlocks.CLAY_TREE_LEAVES, ItemBlockTiered<*>::noSubTypes))
        registry.register(createItemBlock(ClayiumBlocks.CLAY_TREE_SAPLING, ItemBlockTiered<*>::noSubTypes))

        registry.register(createItemBlock(ClayiumBlocks.OVERCLOCKER, ::ItemBlockTiered))
        registry.register(createItemBlock(ClayiumBlocks.ENERGY_STORAGE_UPGRADE, ::ItemBlockTiered))

        registry.register(createItemBlock(ClayiumBlocks.CLAY_MARKER, ::VariantItemBlock))

        registry.register(createItemBlock(ClayiumBlocks.CHUNK_LOADER, ItemBlockTiered<*>::noSubTypes))

        registry.register(createItemBlock(ClayiumBlocks.LASER_REFLECTOR, ::ItemBlockClayLaserReflector))

        for (block in ClayiumBlocks.ENERGIZED_CLAY_BLOCKS) {
            registry.register(createItemBlock(block) { ItemBlockEnergizedClay(it, OrePrefix.block) })
        }
        for (block in ClayiumBlocks.COMPRESSED_CLAY_BLOCKS) {
            registry.register(createItemBlock(block) { ItemBlockMaterial(it, OrePrefix.block) })
        }
    }

    @SubscribeEvent
    @Suppress("unused")
    fun createMteRegistry(e: ClayiumMteRegistryEvent) {
        e.mteManager.createRegistry(MOD_ID)
    }

    open fun registerItem(registry: IForgeRegistry<Item>, item: Item) {
        registry.register(item)
    }

    private fun <T: Block> createItemBlock(block: T, producer: (T) -> ItemBlock): ItemBlock {
        return producer(block).apply {
            registryName = block.registryName ?: throw IllegalArgumentException("Block ${block.translationKey} has no registry name")
        }
    }

    fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileClayWorkTable::class.java, clayiumId("clayWorkTable"))
        GameRegistry.registerTileEntity(TileEntityClayLaserReflector::class.java, clayiumId("laser_reflector"))
        GameRegistry.registerTileEntity(TileEntityCreativeEnergySource::class.java, clayiumId("creativeEnergySource"))
        GameRegistry.registerTileEntity(MetaTileEntityHolder::class.java, clayiumId("metaTileEntityHolder"))
        GameRegistry.registerTileEntity(BlockQuartzCrucible.QuartzCrucibleTileEntity::class.java, clayiumId("quartzCrucibleTileEntity"))
        GameRegistry.registerTileEntity(TileClayCraftingTable::class.java, clayiumId("clayCraftingTable"))

        GameRegistry.registerTileEntity(TileClayMarker.NoExtend::class.java, clayiumId("clayMarkerNoExtent"))
        GameRegistry.registerTileEntity(TileClayMarker.ExtendToGround::class.java, clayiumId("clayMarkerExtendToGround"))
        GameRegistry.registerTileEntity(TileClayMarker.ExtendToSky::class.java, clayiumId("clayMarkerExtendToSky"))
        GameRegistry.registerTileEntity(TileClayMarker.AllHeight::class.java, clayiumId("clayMarkerAllHeight"))

        GameRegistry.registerTileEntity(ChunkLoaderTileEntity::class.java, clayiumId("chunkLoader"))
    }

    /* Client-Only Methods */
    open fun registerCompressedBlockSprite(material: CMaterial) {}
}
