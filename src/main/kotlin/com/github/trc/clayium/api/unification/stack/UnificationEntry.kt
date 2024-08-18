package com.github.trc.clayium.api.unification.stack

import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.ore.OrePrefix

data class UnificationEntry(
    val orePrefix: OrePrefix,
    val material: CMaterial,
) {
    override fun toString(): String {
        return "${orePrefix.camel}${material.upperCamel}"
    }
}