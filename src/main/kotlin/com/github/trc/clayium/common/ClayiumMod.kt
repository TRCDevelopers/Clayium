package com.github.trc.clayium.common

import com.github.trc.clayium.CTags
import com.github.trc.clayium.api.CValues
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
    modid = CValues.MOD_ID,
    name = CValues.MOD_NAME,
    version = CTags.VERSION,
    modLanguageAdapter = "io.github.chaosunity.forgelin.KotlinAdapter",
    dependencies = "required:forge@[14.23.5.2847,);" +
            "required-after:forgelin_continuous@[2.0.0.0,);" +
            "required-after:modularui@[2.4.3,);" +
            "required-after:codechickenlib@[3.2.3,);" +
            "after:jei@[4.15.0,);" + "after:groovyscript@[1.1.3,);" +
            "after:enderio;" + "after:theoneprobe;"
)
object ClayiumMod {

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(CValues.MOD_ID)

    @SidedProxy(clientSide = "com.github.trc.clayium.client.ClientProxy", serverSide = "com.github.trc.clayium.common.CommonProxy")
    lateinit var proxy: CommonProxy

    val creativeTab: CreativeTabs = object : CreativeTabs(getNextID(), CValues.MOD_ID) {
        @SideOnly(Side.CLIENT)
        override fun createIcon(): ItemStack {
            return ItemStack(Items.CLAY_BALL)
        }
    }

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
}
