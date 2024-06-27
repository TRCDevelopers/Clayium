package com.github.trcdevelopers.clayium.common.unification.ore

import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey
import com.google.common.base.CaseFormat
import java.util.function.Predicate

class OrePrefix private constructor(
    val camel: String,
    val itemGenerationLogic: Predicate<Material>? = null,
) {
    init {
        _prefixes.add(this)
    }

    val snake = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel)

    //todo: move to somewhere else?
    fun canGenerateItem(material: Material): Boolean {
        return itemGenerationLogic == null
                || itemGenerationLogic.test(material)
    }

    companion object {
        private val _prefixes = mutableListOf<OrePrefix>()
        val allPrefixes: List<OrePrefix> = _prefixes


        private val hasIngotProperty= Predicate<Material> { it.hasProperty(PropertyKey.INGOT) }
        private val hasDustProperty = Predicate<Material> { it.hasProperty(PropertyKey.DUST) }
        private val hasPlateProperty = Predicate<Material> { it.hasProperty(PropertyKey.PLATE) }
        private val hasMatterProperty = Predicate<Material> { it.hasProperty(PropertyKey.MATTER) }
        private val hasImpureDustProperty = Predicate<Material> { it.hasProperty(PropertyKey.IMPURE_DUST) }


        val ingot = OrePrefix("ingot", hasIngotProperty)
        val dust = OrePrefix("dust", hasDustProperty)
        val plate = OrePrefix("plate", hasPlateProperty)
        val largePlate = OrePrefix("large_plate", hasPlateProperty)
        val matter = OrePrefix("matter", hasMatterProperty)

        val impureDust = OrePrefix("impure_dust", hasImpureDustProperty)

        val block = OrePrefix("block")

        val metaItemPrefixes = listOf(ingot, dust, impureDust, matter, plate, largePlate)
    }
}