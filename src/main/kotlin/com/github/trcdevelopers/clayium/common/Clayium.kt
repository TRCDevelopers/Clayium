package com.github.trcdevelopers.clayium.common

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(modid = Clayium.MOD_ID, name = Clayium.MOD_NAME, version = Clayium.VERSION)
class Clayium {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        // If proxy is null, something is wrong.
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

    companion object {
        const val MOD_ID = "clayium"
        const val MOD_NAME = "Clayium"
        const val VERSION = "1.0-SNAPSHOT"

        val CreativeTab: CreativeTabs = object : CreativeTabs(getNextID(), MOD_ID) {
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
