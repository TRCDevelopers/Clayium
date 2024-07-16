package com.github.trc.clayium.common.unification.stack

import com.github.trc.clayium.common.unification.material.Material
import com.github.trc.clayium.common.unification.ore.OrePrefix

data class UnificationEntry(
    val orePrefix: OrePrefix,
    val material: Material,
) {
    override fun toString(): String {
        return "${orePrefix.camel}${material.upperCamel}"
    }
}