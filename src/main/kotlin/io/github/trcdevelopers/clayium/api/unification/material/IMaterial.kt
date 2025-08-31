package io.github.trcdevelopers.clayium.api.unification.material

import io.github.trcdevelopers.clayium.api.util.ITier

interface IMaterial {
    val upperCamelName: String

    val tier: ITier?
        get() = null
}