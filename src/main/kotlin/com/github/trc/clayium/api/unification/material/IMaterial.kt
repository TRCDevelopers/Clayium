package com.github.trc.clayium.api.unification.material

import com.github.trc.clayium.api.util.ITier

interface IMaterial {
    val upperCamelName: String

    /**
     * the amount of material in a block.
     * default is 9.
     * used for grinder, condenser and some other recipes.
     */
    val blockAmount: Int
        get() = 9

    val tier: ITier?
        get() = null
}