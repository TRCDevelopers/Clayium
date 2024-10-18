package com.github.trc.clayium

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.MOD_ID

object Bootstrap {

    private var bootstrapped = false

    fun perform() {
        if (bootstrapped) return
        bootstrapped = true
        net.minecraft.init.Bootstrap.register()
        ClayiumApi.mteManager.createRegistry(MOD_ID)
    }
}
