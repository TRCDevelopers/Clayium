package com.github.trc.clayium.api.unification.material.registry

import com.github.trc.clayium.api.unification.material.CMarkerMaterial

class CMarkerMaterialRegistry : Iterable<CMarkerMaterial> {
    private val _map = mutableMapOf<String, CMarkerMaterial>()

    fun register(material: CMarkerMaterial): CMarkerMaterial {
        return _map.getOrPut(material.upperCamelName) { material }
    }

    fun getMaterial(name: String): CMarkerMaterial? {
        return _map[name]
    }

    override fun iterator(): Iterator<CMarkerMaterial> {
        return _map.values.toList().iterator()
    }
}
