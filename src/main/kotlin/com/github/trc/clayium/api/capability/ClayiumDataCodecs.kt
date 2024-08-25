package com.github.trc.clayium.api.capability

object ClayiumDataCodecs {
    private var nextId = 0
    fun assignId(): Int = nextId++

    val INITIALIZE_MTE = assignId()
    val UPDATE_FRONT_FACING = assignId()
    val UPDATE_OC_FACTOR = assignId()

    val UPDATE_INPUT_MODE = assignId()
    val UPDATE_OUTPUT_MODE = assignId()
    val UPDATE_FILTER = assignId()
    val UPDATE_CONNECTIONS = assignId()
    val UPDATE_STRUCTURE_VALIDITY = assignId()

    val SYNC_MTE_TRAIT = assignId()

    val UPDATE_LASER = assignId()
    val UPDATE_LASER_ACTIVATION = assignId()

    val UPDATE_ITEMS_STORED = assignId()
    val UPDATE_MAX_ITEMS_STORED = assignId()
    val UPDATE_STORED_ITEMSTACK = assignId()
    val UPDATE_FILTER_ITEM = assignId()

    val UPDATE_RESONANCE = assignId()

    val INTERFACE_SYNC_MIMIC_TARGET = assignId()

    val UPDATE_PAN_DUPLICATION_ENTRIES = assignId()

    const val AUTO_IO_HANDLER = "autoIoHandler"
    const val RECIPE_LOGIC = "recipeLogic"
    const val ENERGY_HOLDER = "energyHolder"
    const val LASER_CONTROLLER = "laserController"
    const val RESONANCE_LISTENER = "resonanceListener"
    const val OVERCLOCK_HANDLER = "overclockHandler"
}