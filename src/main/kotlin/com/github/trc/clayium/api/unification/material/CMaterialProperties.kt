package com.github.trc.clayium.api.unification.material

class CMaterialProperties {
    private val properties: MutableMap<CPropertyKey<*>, MaterialProperty> = mutableMapOf()

    fun <T : MaterialProperty> setProperty(key: CPropertyKey<T>, value: T) {
        properties[key] = value
    }

    fun hasProperty(key: CPropertyKey<*>): Boolean {
        return properties.containsKey(key)
    }

    fun <T : MaterialProperty> getProperty(key: CPropertyKey<T>): T {
        val property = properties[key]
        if (property == null) throw NullPointerException()
        return key.cast(property)
    }

    fun <T: MaterialProperty> getPropOrNull(key: CPropertyKey<T>): T? {
        val property = properties[key] ?: return null
        return key.cast(property)
    }

    fun <T : MaterialProperty> getPropertyOrNull(key: CPropertyKey<T>): T? {
        val property = properties[key]
        return if (property == null) null else key.cast(property)
    }
}