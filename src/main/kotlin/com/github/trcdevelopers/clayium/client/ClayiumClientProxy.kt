package com.github.trcdevelopers.clayium.client

import com.github.trcdevelopers.clayium.client.loader.CeContainerModelLoader
import com.github.trcdevelopers.clayium.client.loader.ClayBufferModelLoader
import com.github.trcdevelopers.clayium.client.tesr.ClayBufferPipeIoRenderer
import com.github.trcdevelopers.clayium.common.ClayiumCommonProxy
import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
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

@SideOnly(Side.CLIENT)
class ClayiumClientProxy : ClayiumCommonProxy() {

    private val metaItems = mutableListOf<MetaItemClayium>()

    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
        ModelLoaderRegistry.registerLoader(CeContainerModelLoader())
        ModelLoaderRegistry.registerLoader(ClayBufferModelLoader)
        ClientRegistry.bindTileEntitySpecialRenderer(TileClayBuffer::class.java, ClayBufferPipeIoRenderer)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
        ClayiumItems.registerItemColors()
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)
        for (item in metaItems) {
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
    }

    @SubscribeEvent
    override fun registerBlocks(event: RegistryEvent.Register<Block>) {
        ClayiumBlocks.registerBlocks(event, Side.CLIENT)
    }

    override fun registerItem(registry: IForgeRegistry<Item>, item: Item) {
        if (item is MetaItemClayium) {
            metaItems.add(item)
            item.registerModels()
        }
        super.registerItem(registry, item)
    }
}
