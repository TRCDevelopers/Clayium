package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.api.util.clayiumId
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy

object CMaterials {
    val impureSilicon = Material.create(0, clayiumId("impure_silicon")) {
        colors(0x978F98, 0x533764, 0xA9A5A5)
        ingot().dust()
        plate(ClayEnergy.milli(1), 20, tier = 4)
    }

    val octupleEnergyClay = Material.create(12, clayiumId("octuple_energy_clay")) {
        colors(0xFFFF00, 0x8C8C8C, 0xFFFFFF)
        dust()
        energizedClay(ClayEnergy.of(100_000_000))
        plate(ClayEnergy.of(10000), 20, tier = 9)
    }

    fun init() {}
}