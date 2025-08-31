package io.github.trcdevelopers.clayium

import io.github.trcdevelopers.clayium.api.ClayiumApi
import io.github.trcdevelopers.clayium.api.MOD_ID

object Bootstrap {

    private var bootstrapped = false

    fun perform() {
        if (bootstrapped) return
        bootstrapped = true
        net.minecraft.init.Bootstrap.register()
        ClayiumApi.mteManager.createRegistry(MOD_ID)
    }
}