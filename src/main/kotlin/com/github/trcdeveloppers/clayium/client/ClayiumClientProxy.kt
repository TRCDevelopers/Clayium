package com.github.trcdeveloppers.clayium.client

import com.github.trcdeveloppers.clayium.client.loader.CeContainerModelLoader
import com.github.trcdeveloppers.clayium.client.loader.ClayBufferModelLoader
import com.github.trcdeveloppers.clayium.common.ClayiumCommonProxy
import com.github.trcdeveloppers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdeveloppers.clayium.common.items.ClayiumItems
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class ClayiumClientProxy : ClayiumCommonProxy() {
    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
        ModelLoaderRegistry.registerLoader(CeContainerModelLoader())
        ModelLoaderRegistry.registerLoader(ClayBufferModelLoader)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
        ClayiumItems.registerItemColors()
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)
    }

    @SubscribeEvent
    override fun registerItems(event: RegistryEvent.Register<Item>) {
        ClayiumItems.registerItems(event, Side.CLIENT)
    }

    @SubscribeEvent
    override fun registerBlocks(event: RegistryEvent.Register<Block>) {
        ClayiumBlocks.registerBlocks(event, Side.CLIENT)
    }

    override fun registerTileEntities() {
        super.registerTileEntities()
    }
}
