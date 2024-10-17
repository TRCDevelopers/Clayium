package com.github.trc.clayium.common

import com.github.trc.clayium.CTags
import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.MOD_NAME
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(
    modid = MOD_ID,
    name = MOD_NAME,
    version = CTags.VERSION,
    modLanguageAdapter = "io.github.chaosunity.forgelin.KotlinAdapter",
    dependencies =
        "required:forge@[14.23.5.2847,);" +
            "required-after:forgelin_continuous@[2.0.0.0,);" +
            "required-after:modularui@[2.4.3,);" +
            "required-after:codechickenlib@[3.2.3,);" +
            "required-after:mixinbooter@[9.1,);" +
            "after:jei@[4.15.0,);" +
            "after:groovyscript@[1.1.3,);" +
            "after:enderio;" +
            "after:theoneprobe;" +
            "after:gregtech;"
)
object ClayiumMod {

    @SidedProxy(
        clientSide = "com.github.trc.clayium.client.ClientProxy",
        serverSide = "com.github.trc.clayium.common.CommonProxy"
    )
    lateinit var proxy: CommonProxy

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
