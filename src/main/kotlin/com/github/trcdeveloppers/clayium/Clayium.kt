package com.github.trcdeveloppers.clayium

import com.github.trcdeveloppers.clayium.common.ClayiumCommonProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(modid = Clayium.MOD_ID, name = Clayium.MOD_NAME, version = Clayium.VERSION)
class Clayium {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        // If proxy is null, something is wrong.
        proxy!!.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy!!.init(event)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        proxy!!.postInit(event)
    }

    companion object {
        const val MOD_ID = "clayium"
        const val MOD_NAME = "Clayium"
        const val VERSION = "1.0-SNAPSHOT"

        @JvmField
        val LOGGER: Logger = LogManager.getLogger(MOD_ID)
        /**
         * This is the instance of your mod as created by Forge. It will never be null.
         */
        @JvmField
        @Mod.Instance(MOD_ID)
        var INSTANCE: Clayium? = null

        @SidedProxy(clientSide = "com.github.trcdeveloppers.clayium.client.ClayiumClientProxy", serverSide = "com.github.trcdeveloppers.clayium.common.ClayiumCommonProxy")
        var proxy: ClayiumCommonProxy? = null
    }
}
