package com.github.trc.clayium.api.unification.material

import com.github.trc.clayium.api.ClayiumApi

/**
 * has a name only.
 * used for registering other-mod material recipes.
 */
data class CMarkerMaterial(
    override val upperCamelName: String,
    override val blockAmount: Int = 9
) : IMaterial {
    init {
        require(upperCamelName.isNotBlank()) { "upperCamelName must not be blank" }

        ClayiumApi.markerMaterials.register(this)
    }
}