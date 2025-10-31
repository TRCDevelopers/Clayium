package io.github.trcdevelopers.clayium.core

import zone.rong.mixinbooter.ILateMixinLoader

@Suppress("unused")
class ClayiumLateMixinLoadingPlugin : ILateMixinLoader {
    override fun getMixinConfigs(): MutableList<String?> {
        return mutableListOf("mixins.clayium.json")
    }
}
