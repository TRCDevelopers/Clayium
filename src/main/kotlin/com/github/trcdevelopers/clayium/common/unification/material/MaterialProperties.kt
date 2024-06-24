package com.github.trcdevelopers.clayium.common.unification.material

class MaterialProperties {
    private val properties: MutableMap<PropertyKey<*>, MaterialProperty> = mutableMapOf()

    fun hasProperty(key: PropertyKey<*>): Boolean {
        return properties.containsKey(key)
    }

    fun <T : MaterialProperty> getProperty(key: PropertyKey<T>): T {
        val property = properties[key]
        if (property == null) throw NullPointerException()
        return key.cast(property)
    }

    fun <T : MaterialProperty> getPropertyOrNull(key: PropertyKey<T>): T? {
        val property = properties[key]
        return if (property == null) null else key.cast(property)
    }
}