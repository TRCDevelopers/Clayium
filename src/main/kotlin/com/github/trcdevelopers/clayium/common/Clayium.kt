package com.github.trcdevelopers.clayium.common

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(
    modid = Clayium.MOD_ID,
    name = Clayium.MOD_NAME,
    version = Clayium.VERSION,
    modLanguageAdapter = "io.github.chaosunity.forgelin.KotlinAdapter",
)
object Clayium {
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

    @SidedProxy(clientSide = "com.github.trcdevelopers.clayium.client.ClientProxy", serverSide = "com.github.trcdevelopers.clayium.common.CommonProxy")
    lateinit var proxy: CommonProxy
}
