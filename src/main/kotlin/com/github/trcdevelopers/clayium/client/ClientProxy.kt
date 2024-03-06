package com.github.trcdevelopers.clayium.client

import com.github.trcdevelopers.clayium.client.loader.ClayBufferModelLoader
import com.github.trcdevelopers.clayium.client.tesr.ClayBufferPipeIoRenderer
import com.github.trcdevelopers.clayium.common.CommonProxy
import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.blocks.clay.ItemBlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.clay.ItemBlockEnergizedClay
import com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer.TileClayBuffer
import com.github.trcdevelopers.clayium.common.items.ClayiumItems
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaPrefixItem
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry

@Suppress("unused")
@SideOnly(Side.CLIENT)
class ClientProxy : CommonProxy() {
    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
        ModelLoaderRegistry.registerLoader(ClayBufferModelLoader)
        ClientRegistry.bindTileEntitySpecialRenderer(TileClayBuffer::class.java, ClayBufferPipeIoRenderer)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
        ClayiumItems.registerItemColors()
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)
        for (item in MetaItemClayium.META_ITEMS) {
            item.registerColorHandler()
        }
    }

    @SubscribeEvent
    override fun registerItems(event: RegistryEvent.Register<Item>) {
        val registry = event.registry

        for (orePrefix in OrePrefix.entries) {
            val metaPrefixItem = MetaPrefixItem.create("meta_${orePrefix.snake}", orePrefix)
            registry.register(metaPrefixItem)
            metaPrefixItem.registerSubItems()
            metaPrefixItem.registerModels()
        }
        ClayiumItems.registerItems(event, Side.CLIENT)

        registry.register(ItemBlockCompressedClay(ClayiumBlocks.COMPRESSED_CLAY).apply { registerModels() })
        registry.register(ItemBlockEnergizedClay(ClayiumBlocks.ENERGIZED_CLAY).apply { registerModels() })
    }

    @SubscribeEvent
    override fun registerBlocks(event: RegistryEvent.Register<Block>) {
        val registry = event.registry
        ClayiumBlocks.registerBlocks(event, Side.CLIENT)

        registry.register(ClayiumBlocks.COMPRESSED_CLAY)
        registry.register(ClayiumBlocks.ENERGIZED_CLAY)
    }

    override fun registerItem(registry: IForgeRegistry<Item>, item: Item) {
        if (item is MetaItemClayium) {
            item.registerModels()
        }
        super.registerItem(registry, item)
    }
}
