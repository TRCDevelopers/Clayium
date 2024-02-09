package com.github.trcdevelopers.clayium.common.unification

import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty

class OrePrefix(
    val name: String,
    val generateCondition: (Material) -> Boolean,
) {

    fun doGenerateItem(material: Material): Boolean {
        return generateCondition(material)
    }

    companion object {
        val ingot = OrePrefix("ingot") { it.hasProperty<MaterialProperty.Ingot>() }
        val matter = OrePrefix("matter") { it.hasProperty<MaterialProperty.Matter>() }
        val dust = OrePrefix("dust") { it.hasProperty<MaterialProperty.Dust>() }
        val plate = OrePrefix("plate") { it.hasProperty<MaterialProperty.Plate>() }
    }
}