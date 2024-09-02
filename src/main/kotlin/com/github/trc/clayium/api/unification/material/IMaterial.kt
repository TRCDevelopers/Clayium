package com.github.trc.clayium.api.unification.material

interface IMaterial {
    val upperCamelName: String

    /**
     * the amount of material in a block.
     * default is 9.
     * used for grinder, condenser and some other recipes.
     */
    val blockAmount: Int
        get() = 9
}