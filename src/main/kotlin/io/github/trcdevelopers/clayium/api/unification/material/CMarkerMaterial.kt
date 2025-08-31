package io.github.trcdevelopers.clayium.api.unification.material

import io.github.trcdevelopers.clayium.api.ClayiumApi

/**
 * has a name only.
 * used for registering other-mod material recipes.
 */
data class CMarkerMaterial(
    override val upperCamelName: String,
) : IMaterial {
    init {
        require(upperCamelName.isNotBlank()) { "upperCamelName must not be blank" }

        ClayiumApi.markerMaterials.register(this)
    }
}