package com.github.trcdevelopers.clayium.common.unification.material

class MaterialProperties() {

    val properties: MutableSet<MaterialProperty> = mutableSetOf()

    inline fun <reified T: MaterialProperty> addProperty(property: MaterialProperty) {
        if (hasProperty<T>()) {
            throw IllegalArgumentException("Material already has property of type ${T::class.simpleName}")
        }
        properties.add(property)
    }

    inline fun <reified T: MaterialProperty> hasProperty(): Boolean {
        return properties.filterIsInstance<T>().isNotEmpty()
    }
}