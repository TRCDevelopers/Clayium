package io.github.trcdevelopers.clayium.common

import com.cleanroommc.modularui.factory.GuiManager
import io.github.trcdevelopers.clayium.api.ClayiumApi
import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.capability.SimpleCapabilityManager
import io.github.trcdevelopers.clayium.api.events.ClayiumMteRegistryEvent
import io.github.trcdevelopers.clayium.api.gui.MetaTileEntityGuiFactory
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.util.CLog
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.blocks.BlockQuartzCrucible
import io.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import io.github.trcdevelopers.clayium.common.blocks.TileEntityClayLaserReflector
import io.github.trcdevelopers.clayium.common.blocks.TileEntityCreativeEnergySource
import io.github.trcdevelopers.clayium.common.blocks.chunkloader.ChunkLoaderTileEntity
import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingTable
import io.github.trcdevelopers.clayium.common.blocks.clayworktable.TileClayWorkTable
import io.github.trcdevelopers.clayium.common.blocks.marker.TileClayMarker
import io.github.trcdevelopers.clayium.common.blocks.metalchest.BlockMetalChest
import io.github.trcdevelopers.clayium.common.blocks.metalchest.TileEntityMetalChest
import io.github.trcdevelopers.clayium.common.creativetab.ClayiumCTabs
import io.github.trcdevelopers.clayium.common.event.EntityEventListener
import io.github.trcdevelopers.clayium.common.items.ClayiumItems
import io.github.trcdevelopers.clayium.common.items.ItemClaySteelTool
import io.github.trcdevelopers.clayium.common.loaders.OreDictionaryLoader
import io.github.trcdevelopers.clayium.common.loaders.recipe.CRecipeLoader
import io.github.trcdevelopers.clayium.common.metatileentities.MetaTileEntities
import io.github.trcdevelopers.clayium.common.network.CNetwork
import io.github.trcdevelopers.clayium.common.pan.factories.CPanRecipeFactory
import io.github.trcdevelopers.clayium.common.pan.factories.CraftingTablePanRecipeFactory
import io.github.trcdevelopers.clayium.common.pan.factories.FurnacePanRecipeFactory
import io.github.trcdevelopers.clayium.common.unification.ClayiumOreDictUnifierImpl
import io.github.trcdevelopers.clayium.common.util.DebugUtils
import io.github.trcdevelopers.clayium.common.worldgen.ClayOreGenerator
import io.github.trcdevelopers.clayium.datafix.ClayiumDataFix
import io.github.trcdevelopers.clayium.integration.CModIntegration
import io.github.trcdevelopers.clayium.integration.gregtech.GTOreDictUnifierAdapter
import io.github.trcdevelopers.clayium.network.ClayChunkLoaderCallback
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
        MinecraftForge.EVENT_BUS.register(ItemClaySteelTool)
        MinecraftForge.EVENT_BUS.register(EntityEventListener)
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

        BlockMetalChest.loadMetalChestConfig()

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
        ClayiumDataFix.init()
    }

    open fun postInit(event: FMLPostInitializationEvent) {
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
        ClayiumBlocks.registerBlocks(event.registry)
    }

    //todo move to ClayiumBlocks/Items
    @Suppress("unused")
    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        CLog.info("Registering items...")
        val registry = event.registry

        ClayiumItems.registerItems(registry)
        ClayiumBlocks.registerItemBlocks(registry)
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

        GameRegistry.registerTileEntity(TileEntityMetalChest::class.java, clayiumId("metalChest"))
    }

    open fun updateFlightStatus(mode: Int) {}
    open fun overclockPlayer(delay: Int) {}
}
