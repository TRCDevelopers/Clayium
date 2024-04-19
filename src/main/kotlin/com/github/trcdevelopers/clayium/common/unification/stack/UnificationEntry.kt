package com.github.trcdevelopers.clayium.common.unification.stack

import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material

data class UnificationEntry(
    val orePrefix: OrePrefix,
    val material: Material,
) {
    override fun toString(): String {
        return "$orePrefix:${material.toUpperCamel()}"
    }
}