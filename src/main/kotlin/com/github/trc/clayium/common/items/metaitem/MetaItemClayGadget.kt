package com.github.trc.clayium.common.items.metaitem

object MetaItemClayGadget : MetaItemClayium("clay_gadget") {
    val OverclockMk1 = addItem(0, "overclock_mk1").tier(10)
    val OverclockMk2 = addItem(1, "overclock_mk2").tier(11)
    val OverclockMk3 = addItem(2, "overclock_mk3").tier(12)
    val OverclockMk4 = addItem(3, "overclock_mk4").tier(13)

    val FlightMk1 = addItem(100, "flight_mk1").tier(12)
    val FlightMk2 = addItem(101, "flight_mk2").tier(13)
    val FlightMk3 = addItem(102, "flight_mk3").tier(13)

    val HealthMk1 = addItem(200, "health_mk1").tier(6)
    val HealthMk2 = addItem(201, "health_mk2").tier(10)
    val HealthMk3 = addItem(202, "health_mk3").tier(12)
}