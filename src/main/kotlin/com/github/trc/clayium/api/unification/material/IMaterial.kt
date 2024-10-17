package com.github.trc.clayium.api.unification.material

import com.github.trc.clayium.api.util.ITier

interface IMaterial {
    val upperCamelName: String

    val tier: ITier?
        get() = null
}
