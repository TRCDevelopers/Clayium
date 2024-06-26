package com.github.trcdevelopers.clayium.common.unification

import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty
import com.google.common.base.CaseFormat

enum class EnumOrePrefix(
    val camel: String,
    val snake: String = CUtils.toLowerSnake(camel),
) {

    INGOT("ingot") {
        override fun doGenerateItem(material: EnumMaterial) = material.hasProperty<MaterialProperty.Ingot>()
    },
    MATTER("matter") {
        override fun doGenerateItem(material: EnumMaterial) = material.hasProperty<MaterialProperty.Matter>()
    },
    DUST("dust") {
        override fun doGenerateItem(material: EnumMaterial) = material.hasProperty<MaterialProperty.Dust>()
    },
    IMPURE_DUST("impureDust") {
        override fun doGenerateItem(material: EnumMaterial) = material.hasProperty<MaterialProperty.ImpureDust>()
    },
    PLATE("plate") {
        override fun doGenerateItem(material: EnumMaterial) = material.hasProperty<MaterialProperty.Plate>()
    },
    LARGE_PLATE("largePlate") {
        override fun doGenerateItem(material: EnumMaterial) = material.hasProperty<MaterialProperty.Plate>()
    },
    ;

    abstract fun doGenerateItem(material: EnumMaterial): Boolean

    fun concat(material: EnumMaterial) = "$camel${CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, material.materialName)}"
}