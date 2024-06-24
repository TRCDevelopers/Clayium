package com.github.trcdevelopers.clayium.common.unification.material

class PropertyKey<T : MaterialProperty>  {
    fun cast(value: MaterialProperty): T {
        return value as T
    }

    companion object {
        val INGOT = PropertyKey<MaterialProperty.Ingot>()
    }
}