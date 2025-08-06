package io.github.trcdevelopers.clayium.api.unification.stack

import io.github.trcdevelopers.clayium.api.unification.material.IMaterial
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix

data class UnificationEntry(
    val orePrefix: OrePrefix,
    val material: IMaterial,
) {
    override fun toString(): String {
        return "${orePrefix.camel}${material.upperCamelName}"
    }
}