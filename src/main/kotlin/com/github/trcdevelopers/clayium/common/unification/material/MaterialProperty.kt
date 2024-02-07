package com.github.trcdevelopers.clayium.common.unification.material

sealed interface MaterialProperty {
    fun verify(material: Material): Boolean

    data object Ingot : MaterialProperty { override fun verify(material: Material) = true }
    data object Matter : MaterialProperty { override fun verify(material: Material) = true }

    class Plate(
        val recipeTime: Int,
    ) : MaterialProperty {
        override fun verify(material: Material) = material.properties.hasProperty<Ingot>() || material.properties.hasProperty<Matter>()


    }
}
