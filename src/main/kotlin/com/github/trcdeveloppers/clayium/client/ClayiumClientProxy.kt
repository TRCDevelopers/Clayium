package com.github.trcdeveloppers.clayium.client

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.Clayium.Companion.LOGGER
import com.github.trcdeveloppers.clayium.client.loader.CeContainerModelLoader
import com.github.trcdeveloppers.clayium.client.model.CeContainerBakedModel
import com.github.trcdeveloppers.clayium.common.ClayiumCommonProxy
import com.github.trcdeveloppers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdeveloppers.clayium.common.blocks.machine.BlockTestSingleSlotMachine
import com.github.trcdeveloppers.clayium.common.items.ClayiumItems
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

class ClayiumClientProxy : ClayiumCommonProxy() {
    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
        ModelLoaderRegistry.registerLoader(CeContainerModelLoader())
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

//    @SubscribeEvent
//    fun onModelBakeEvent(event: ModelBakeEvent) {
//        Clayium.LOGGER.info("onModelBakeEvent called")
//        for (state in BlockTestSingleSlotMachine.INSTANCE.blockState.validStates) {
//            event.modelRegistry.putObject(
//                event.modelManager.blockModelShapes.blockStateMapper.getVariants(BlockTestSingleSlotMachine.INSTANCE)[state] ?: continue,
//                CeContainerBakedModel(ModelLoader.defaultTextureGetter(), 6))
//        }
//    }
}
