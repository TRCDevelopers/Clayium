package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.api.util.clayiumId
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy

object CMaterials {

    val clay = Material.create(0, clayiumId("clay")) {
        tier(0)
        clay()
        dust()
        plate(ClayEnergy.micro(10), 1, tier = 0)
    }

    val denseClay = Material.create(1, clayiumId("dense_clay")) {
        tier(0)
        clay(clay)
        dust()
        plate(ClayEnergy.micro(10), 4, tier = 0)
    }

    val compressedClay = Material.create(2, clayiumId("compressed_clay")) {
        tier(1)
        clay(denseClay)
    }

    val industrialClay = Material.create(3, clayiumId("industrial_clay")) {
        tier(2)
        clay(compressedClay)
        dust()
        plate(ClayEnergy.micro(20), 4, tier = 2)
    }

    val advancedIndustrialClay = Material.create(4, clayiumId("advanced_industrial_clay")) {
        tier(3)
        clay(industrialClay)
        dust()
        plate(ClayEnergy.micro(40), 4, tier = 3)
    }

    val energeticClay = Material.create(5, clayiumId("energetic_clay")) {
        tier(4)
        clay(advancedIndustrialClay, ClayEnergy.of(1))
    }

    val compressedEnergeticClay = Material.create(6, clayiumId("compressed_energetic_clay")) {
        tier(5)
        clay(energeticClay, ClayEnergy.of(10))
    }

    val compressedEnergeticClay2 = Material.create(7, clayiumId("compressed_energetic_clay2")) {
        tier(6)
        clay(compressedEnergeticClay, ClayEnergy.of(100))
    }

    val compressedEnergeticClay3 = Material.create(8, clayiumId("compressed_energetic_clay3")) {
        tier(7)
        clay(compressedEnergeticClay2, ClayEnergy.of(1_000))
    }

    val compressedEnergeticClay4 = Material.create(9, clayiumId("compressed_energetic_clay4")) {
        tier(8)
        clay(compressedEnergeticClay3, ClayEnergy.of(10_000))
    }

    val compressedEnergeticClay5 = Material.create(10, clayiumId("compressed_energetic_clay5")) {
        tier(9)
        clay(compressedEnergeticClay4, ClayEnergy.of(100_000))
    }

    val compressedEnergeticClay6 = Material.create(11, clayiumId("compressed_energetic_clay6")) {
        tier(10)
        clay(compressedEnergeticClay5, ClayEnergy.of(1_000_000))
    }

    val compressedEnergeticClay7 = Material.create(12, clayiumId("compressed_energetic_clay7")) {
        tier(11)
        clay(compressedEnergeticClay6, ClayEnergy.of(10_000_000))
    }

    val octupleEnergyClay = Material.create(13, clayiumId("octuple_energy_clay")) {
        tier(12)
        colors(0xFFFF00, 0x8C8C8C, 0xFFFFFF)
        dust()
        clay(compressedEnergeticClay7, ClayEnergy.of(100_000_000))
        plate(ClayEnergy.of(10000), 20, tier = 9)
    }

    val impureSilicon = Material.create(20, clayiumId("impure_silicon")) {
        colors(0x978F98, 0x533764, 0xA9A5A5)
        ingot().dust()
        plate(ClayEnergy.milli(1), 20, tier = 4)
    }

    // don't use builder so it is not registered
    val DUMMY = Material(0, clayiumId("dummy"), MaterialProperties())

    fun init() {}
}