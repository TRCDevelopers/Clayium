package com.github.trcdevelopers.clayium.common.unification.stack

import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial

data class UnificationEntry(
    val orePrefix: OrePrefix,
    val material: EnumMaterial,
) {
    override fun toString(): String {
        return "${orePrefix.camel}${material.toUpperCamel()}"
    }
}