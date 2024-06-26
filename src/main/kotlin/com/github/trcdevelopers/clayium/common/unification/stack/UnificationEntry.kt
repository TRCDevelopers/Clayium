package com.github.trcdevelopers.clayium.common.unification.stack

import com.github.trcdevelopers.clayium.common.unification.EnumOrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial

data class UnificationEntry(
    val enumOrePrefix: EnumOrePrefix,
    val material: EnumMaterial,
) {
    override fun toString(): String {
        return "${enumOrePrefix.camel}${material.toUpperCamel()}"
    }
}