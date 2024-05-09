package com.github.trcdevelopers.clayium.api.capability

object ClayiumDataCodecs {
    private var nextId = 0
    fun assignId(): Int = nextId++

    val INITIALIZE_MTE = assignId()
    val UPDATE_FRONT_FACING = assignId()

    val UPDATE_INPUT_MODE = assignId()
    val UPDATE_OUTPUT_MODE = assignId()
    val UPDATE_CONNECTIONS = assignId()

    val SYNC_MTE_TRAIT = assignId()

    const val AUTO_IO_HANDLER = "autoIoHandler"
    const val RECIPE_LOGIC = "recipeLogic"
    const val ENERGY_HOLDER = "energyHolder"

    object Translation {
        const val CLAY_ENERGY = "tooltip.clayium.ce"
        const val MACHINE_TIER = "machine.clayium.tier"
    }
}