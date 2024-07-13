package com.github.trc.clayium.client

import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trc.clayium.client.model.LaserReflectorModelLoader
import com.github.trc.clayium.client.model.MetaTileEntityModelLoader
import com.github.trc.clayium.client.renderer.ClayLaserReflectorRenderer
import com.github.trc.clayium.client.renderer.MetaTileEntityRenderDispatcher
import com.github.trc.clayium.common.CommonProxy
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.blocks.TileEntityClayLaserReflector
import com.github.trc.clayium.common.items.metaitem.MetaItemClayium
import com.github.trc.clayium.common.metatileentity.MetaTileEntities
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
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
        ModelLoaderRegistry.registerLoader(MetaTileEntityModelLoader)
        ModelLoaderRegistry.registerLoader(LaserReflectorModelLoader)
        ClientRegistry.bindTileEntitySpecialRenderer(MetaTileEntityHolder::class.java, MetaTileEntityRenderDispatcher)
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityClayLaserReflector::class.java, ClayLaserReflectorRenderer)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)
        MetaItemClayium.registerColors()
    }

    override fun registerItem(registry: IForgeRegistry<Item>, item: Item) {
        registry.register(item)
        if (item is MetaItemClayium) {
            item.registerModels()
        }
        ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName!!, "inventory"))
    }

    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        MetaItemClayium.registerModels()
        ClayiumBlocks.registerModels()
        MetaTileEntities.registerItemModels()
    }
}
