package com.github.trc.clayium.api.unification.material

/**
 * has a name only.
 * used for registering other-mod material recipes.
 */
data class CMarkerMaterial(override val upperCamelName: String) : IMaterial