package com.github.trcdevelopers.clayium.common

import com.github.trcdevelopers.clayium.common.items.metaitem.MetaPrefixItem
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(modid = Clayium.MOD_ID, name = Clayium.MOD_NAME, version = Clayium.VERSION)
class Clayium {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
        proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init(event)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        proxy.postInit(event)
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        val registry: IForgeRegistry<Item> = event.registry

        for (orePrefix in OrePrefix.entries) {
            val metaPrefixItem = MetaPrefixItem.create("meta_${orePrefix.snake}", orePrefix)
            metaPrefixItem.registerSubItems()
            proxy.registerItem(registry, metaPrefixItem)
        }
    }

    companion object {
        const val MOD_ID = "clayium"
        const val MOD_NAME = "Clayium"
        const val VERSION = "1.0-SNAPSHOT"

        val creativeTab: CreativeTabs = object : CreativeTabs(getNextID(), MOD_ID) {
            @SideOnly(Side.CLIENT)
            override fun createIcon(): ItemStack {
                return ItemStack(Items.CLAY_BALL)
            }
        }

        @JvmField
        val LOGGER: Logger = LogManager.getLogger(MOD_ID)
        /**
         * This is the instance of your mod as created by Forge. It will never be null.
         */
        @Mod.Instance(MOD_ID)
        lateinit var INSTANCE: Clayium

        @SidedProxy(clientSide = "com.github.trcdevelopers.clayium.client.ClayiumClientProxy", serverSide = "com.github.trcdevelopers.clayium.common.ClayiumCommonProxy")
        lateinit var proxy: ClayiumCommonProxy
    }
}
