package com.github.trc.clayium

object Bootstrap {

    private var bootstrapped = false

    fun perform() {
        if (bootstrapped) return
        bootstrapped = true
        net.minecraft.init.Bootstrap.register()
    }
}