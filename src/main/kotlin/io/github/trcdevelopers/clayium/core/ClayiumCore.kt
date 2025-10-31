package io.github.trcdevelopers.clayium.core

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import zone.rong.mixinbooter.IEarlyMixinLoader

class ClayiumCore : IFMLLoadingPlugin, IEarlyMixinLoader {
    override fun getASMTransformerClass(): Array<out String> {
        return emptyArray()
    }

    override fun getModContainerClass(): String? {
        return null
    }

    override fun getSetupClass(): String? {
        return null
    }

    override fun injectData(data: Map<String, Any>) {}

    override fun getAccessTransformerClass(): String? {
        return null
    }

    override fun getMixinConfigs(): List<String> {
        return listOf("mixins.core.clayium.json")
    }
}