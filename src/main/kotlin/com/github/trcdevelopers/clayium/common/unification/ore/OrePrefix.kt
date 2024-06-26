package com.github.trcdevelopers.clayium.common.unification.ore

import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey
import java.util.function.Predicate

class OrePrefix private constructor(
    val camel: String,
    val itemGenerationLogic: Predicate<Material>? = null,
) {

    //todo: move to somewhere else?
    fun canGenerateItem(material: Material): Boolean {
        return itemGenerationLogic == null
                || itemGenerationLogic.test(material)
    }

    companion object {
        private val hasIngotProperty: (Material) -> Boolean = { it.hasProperty(PropertyKey.INGOT) }

        val ingot = OrePrefix("ingot", hasIngotProperty)

        val block = OrePrefix("block")
    }
}