package io.github.trcdevelopers.clayium.common

import io.github.trcdevelopers.clayium.CTags
import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.MOD_NAME
import io.github.trcdevelopers.clayium.api.util.CUtils
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator

@Mod(
    modid = MOD_ID,
    name = MOD_NAME,
    version = CTags.VERSION,
    acceptedMinecraftVersions = "[1.12.2,1.13)",
    modLanguageAdapter = "io.github.chaosunity.forgelin.KotlinAdapter",
    dependencies = "required:forge@[14.23.5.2847,);" +
            "required-after:forgelin_continuous@[2.0.0.0,);" +
            "required-after:modularui@[3.0.4,);" +
            "required-after:codechickenlib@[3.2.3,);" +
            "required-after:mixinbooter@[9.1,);" +
            "after:jei@[4.15.0,);" + "after:groovyscript@[1.1.3,);" +
            "after:enderio;" + "after:theoneprobe;" + "after:gregtech;"
)
object ClayiumMod {

    @SidedProxy(clientSide = "io.github.trcdevelopers.clayium.client.ClientProxy", serverSide = "io.github.trcdevelopers.clayium.common.CommonProxy")
    lateinit var proxy: CommonProxy

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
        if (CUtils.isDeobfEnvironment) Configurator.setLevel(MOD_ID, Level.DEBUG)
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
