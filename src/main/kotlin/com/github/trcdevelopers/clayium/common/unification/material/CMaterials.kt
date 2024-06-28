package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.api.util.clayiumId
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy

@Suppress("JoinDeclarationAndAssignment")
object CMaterials {

    val clay: Material
    val denseClay: Material
    val compressedClay: Material
    val industrialClay: Material
    val advancedIndustrialClay: Material
    val energeticClay: Material
    val compressedEnergeticClay: Material
    val compressedEnergeticClay2: Material
    val compressedEnergeticClay3: Material
    val compressedEnergeticClay4: Material
    val compressedEnergeticClay5: Material
    val compressedEnergeticClay6: Material
    val compressedEnergeticClay7: Material
    val octupleEnergyClay: Material

    val impureSilicon: Material
    val silicone: Material
    val silicon: Material
    val aluminum: Material
    val claySteel: Material
    val clayium: Material
    val ultimateAlloy: Material
    val antimatter: Material
    val pureAntimatter: Material
    val octuplePureAntimatter: Material

    val pureAntimatter1: Material
    val pureAntimatter2: Material
    val pureAntimatter3: Material
    val pureAntimatter4: Material
    val pureAntimatter5: Material
    val pureAntimatter6: Material
    val pureAntimatter7: Material

    val steel: Material

    val salt: Material
    val organicClay: Material

    // don't use builder so it is not registered
    val DUMMY = Material(0, clayiumId("dummy"), MaterialProperties())

    init {
        //region clay
        octupleEnergyClay = Material.create(13, clayiumId("octuple_energy_clay")) {
            tier(12)
            colors(0xFFFF00, 0x8C8C8C, 0xFFFFFF)
            dust()
            clay(energy = ClayEnergy.of(100_000_000))
            plate(ClayEnergy.of(10000), 20, tier = 9)
        }

        compressedEnergeticClay7 = Material.create(12, clayiumId("compressed_energetic_clay7")) {
            tier(11)
            clay(octupleEnergyClay, ClayEnergy.of(10_000_000))
        }

        compressedEnergeticClay6 = Material.create(11, clayiumId("compressed_energetic_clay6")) {
            tier(10)
            clay(compressedEnergeticClay7, ClayEnergy.of(1_000_000))
        }

        compressedEnergeticClay5 = Material.create(10, clayiumId("compressed_energetic_clay5")) {
            tier(9)
            clay(compressedEnergeticClay6, ClayEnergy.of(100_000))
        }

        compressedEnergeticClay4 = Material.create(9, clayiumId("compressed_energetic_clay4")) {
            tier(8)
            clay(compressedEnergeticClay5, ClayEnergy.of(10_000))
        }

        compressedEnergeticClay3 = Material.create(8, clayiumId("compressed_energetic_clay3")) {
            tier(7)
            clay(compressedEnergeticClay4, ClayEnergy.of(1_000))
        }

        compressedEnergeticClay2 = Material.create(7, clayiumId("compressed_energetic_clay2")) {
            tier(6)
            clay(compressedEnergeticClay3, ClayEnergy.of(100))
        }

        compressedEnergeticClay = Material.create(6, clayiumId("compressed_energetic_clay")) {
            tier(5)
            clay(compressedEnergeticClay2, ClayEnergy.of(10))
        }

        energeticClay = Material.create(5, clayiumId("energetic_clay")) {
            tier(4)
            clay(compressedEnergeticClay, ClayEnergy.of(1))
        }

        advancedIndustrialClay = Material.create(4, clayiumId("advanced_industrial_clay")) {
            tier(3)
            clay(energeticClay)
            dust()
            plate(ClayEnergy.micro(40), 4, tier = 3)
        }

        industrialClay = Material.create(3, clayiumId("industrial_clay")) {
            tier(2)
            clay(advancedIndustrialClay)
            dust()
            plate(ClayEnergy.micro(20), 4, tier = 2)
        }

        compressedClay = Material.create(2, clayiumId("compressed_clay")) {
            tier(1)
            clay(industrialClay)
        }

        denseClay = Material.create(1, clayiumId("dense_clay")) {
            tier(0)
            clay(compressedClay)
            dust()
            plate(ClayEnergy.micro(10), 4, tier = 0)
        }

        clay = Material.create(0, clayiumId("clay")) {
            tier(0)
            clay(denseClay)
            dust()
            plate(ClayEnergy.micro(10), 1, tier = 0)
        }
        //endregion clay

        impureSilicon = Material.create(20, clayiumId("impure_silicon")) {
            tier(5)
            colors(0x978F98, 0x533764, 0xA9A5A5)
            ingot().dust()
            plate(ClayEnergy.milli(1), 20, tier = 4)
        }

        silicone = Material.create(21, clayiumId("silicone")) {
            tier(5)
            colors(0xD2D2D2, 0xB4B4B4, 0xF0F0F0)
            ingot().dust()
            plate(ClayEnergy.milli(1), 4, tier = 4)
        }

        silicon = Material.create(22, clayiumId("silicon")) {
            tier(5)
            colors(0x281C28, 0x191919, 0xFFFFFF)
            ingot().dust()
            plate(ClayEnergy.milli(1), 20, tier = 4)
        }

        aluminum = Material.create(23, clayiumId("aluminum")) {
            tier(6)
            colors(0xBEC8CA, 0x191919, 0xFFFFFF)
            ingot().dust().impureDust(0xBEC8CA, 0x78783C, 0xDCDCDC)
            plate(ClayEnergy.milli(1), 20, tier = 4)
        }

        claySteel = Material.create(24, clayiumId("clay_steel")) {
            tier(7)
            colors(0x8890AD, 0x191919, 0xFFFFFF)
            ingot().dust()
            plate(ClayEnergy.milli(1), 60, tier = 4)
        }

        clayium = Material.create(25, clayiumId("clayium")) {
            tier(8)
            colors(0x5AF0D2, 0x3F4855, 0xFFCDC8)
            ingot().dust()
            plate(ClayEnergy.milli(1), 120, tier = 4)
        }

        ultimateAlloy = Material.create(26, clayiumId("ultimate_alloy")) {
            tier(9)
            colors(0x55CD55, 0x191919, 0xF5A0FF)
            ingot().dust()
            plate(ClayEnergy.milli(1), 180, tier = 4)
        }

        antimatter = Material.create(27, clayiumId("antimatter")) {
            tier(10)
            colors(0x0000EB, 0x000000, 0xFFFFFF)
            matter().dust()
            plate(ClayEnergy.of(100), 20, tier = 9)
        }

        pureAntimatter = Material.create(28, clayiumId("pure_antimatter")) {
            tier(11)
            colors(0xFF32FF, 0x000000, 0xFFFFFF)
            matter().dust()
            plate(ClayEnergy.of(100), 20, tier = 9)
        }

        octuplePureAntimatter = Material.create(29, clayiumId("octuple_pure_antimatter")) {
            tier(13).colors(0x960000, 0xC8C800, 0xFFFFFF)
            matter("matter5").dust()
            plate(ClayEnergy.of(100), 20, tier = 9)
        }

        pureAntimatter1 = Material.create(30, clayiumId("pure_antimatter1")) {
            tier(11).colors(0xC42385, 0x191919, 0xFFFFFF)
            matter("matter2")
        }

        pureAntimatter2 = Material.create(31, clayiumId("pure_antimatter2")) {
            tier(11).colors(0x8E1777, 0x323200, 0xFFFFFF)
            matter("matter2")
        }

        pureAntimatter3 = Material.create(32, clayiumId("pure_antimatter3")) {
            tier(11).colors(0x5E0D45, 0x4B4B00, 0xFFFFFF)
            matter("matter3")
        }

        pureAntimatter4 = Material.create(33, clayiumId("pure_antimatter4")) {
            tier(12).colors(0x32061F, 0x646400, 0xFFFFFF)
            matter("matter3")
        }

        pureAntimatter5 = Material.create(34, clayiumId("pure_antimatter5")) {
            tier(12).colors(0x520829, 0x7D7D00, 0xFFFFFF)
            matter("matter4")
        }

        pureAntimatter6 = Material.create(35, clayiumId("pure_antimatter6")) {
            tier(12).colors(0x6E0727, 0x969600, 0xFFFFFF)
            matter("matter4")
        }

        pureAntimatter7 = Material.create(36, clayiumId("pure_antimatter7")) {
            tier(13).colors(0x840519, 0xAFAF00, 0xFFFFFF)
            matter("matter4")
        }

        steel = Material.create(1000, clayiumId("steel")) {
            tier(3)
            colors(0x5A5A6E, 0x000000, 0xFFFFFF)
            ingot().dust()
        }

        salt = Material.create(1012, clayiumId("salt")) {
            tier(4)
            colors(0xFFFFFF, 0x8C8C8C, 0xFFFFFF)
            dust()
            plate(ClayEnergy.micro(10), 1, tier = 0)
        }

        organicClay = Material.create(1013, clayiumId("organic_clay")) {
            tier(5)
            colors(0x8890AD, 0x6A2C2B, 0x92A4B7)
            dust()
        }
    }

    fun init() {}
}