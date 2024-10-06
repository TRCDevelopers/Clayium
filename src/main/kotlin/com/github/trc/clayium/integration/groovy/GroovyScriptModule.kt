package com.github.trc.clayium.integration.groovy

import com.cleanroommc.groovyscript.GroovyScript
import com.cleanroommc.groovyscript.api.GroovyPlugin
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer
import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.MOD_NAME
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraftforge.fml.common.Optional

@Optional.Interface(
    iface = "com.cleanroommc.groovyscript.api.GroovyPlugin",
    modid = Mods.Names.GROOVY_SCRIPT,
    striprefs = true,
)
class GroovyScriptModule : GroovyPlugin {
    @Optional.Method(modid = Mods.Names.GROOVY_SCRIPT)
    override fun getModId(): String {
        return MOD_ID
    }

    @Optional.Method(modid = Mods.Names.GROOVY_SCRIPT)
    override fun getContainerName(): String {
        return MOD_NAME
    }

    @Optional.Method(modid = Mods.Names.GROOVY_SCRIPT)
    override fun onCompatLoaded(container: GroovyContainer<*>) {
        CRecipes.ALL_REGISTRIES.values.forEach { r ->
            container.addProperty(r.grsVirtualizedRegistry)
        }
        container.addProperty(CRecipes.LASER.grsVirtualizedRegistry)
    }

    companion object {
        fun isCurrentlyRunning(): Boolean {
            return Mods.GroovyScript.isModLoaded
                    && GroovyScript.getSandbox().isRunning
        }
    }
}