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

    val actinium: Material
    val americium: Material
    val antimony: Material
    val barium: Material
    val beryllium: Material
    val bismuth: Material
    val caesium: Material
    val calcium: Material
    val cerium: Material
    val chromium: Material
    val cobalt: Material
    val copper: Material
    val curium: Material
    val europium: Material
    val francium: Material
    val hafnium: Material
    val iridium: Material
    val lanthanum: Material
    val lead: Material
    val lithium: Material
    val magnesium: Material
    val manganese: Material
    val molybdenum: Material
    val neodymium: Material
    val neptunium: Material
    val osmium: Material
    val palladium: Material
    val platinum: Material
    val plutonium: Material
    val potassium: Material
    val praseodymium: Material
    val promethium: Material
    val protactinium: Material
    val radium: Material
    val rhenium: Material
    val rubidium: Material
    val samarium: Material
    val silver: Material
    val sodium: Material
    val strontium: Material
    val tantalum: Material
    val thorium: Material
    val tin: Material
    val titanium: Material
    val tungsten: Material
    val uranium: Material
    val vanadium: Material
    val zinc: Material
    val zirconium: Material

    val brass: Material
    val bronze: Material
    val electrum: Material
    val invar: Material
    val nickel: Material
    val steel: Material
    val zinc_aluminum: Material
    val zinc_zirconium: Material

    val salt: Material
    val organicClay: Material

    // don't use builder so it is not registered
    val DUMMY = Material(0, clayiumId("dummy"), MaterialProperties())

    init {
        //region Clay
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
        //endregion

        //region Core Materials
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
        //endregion

        //region Element Materials
        actinium = Material.create(100, clayiumId("actinium")) {
            tier(7).colors(0x8E1777, 0x323200, 0xFFFFFF)
            ingot().dust()
            impureDust(0x8E1777, 0x78783C, 0xDCDCDC)
        }
        americium = Material.create(101, clayiumId("americium")) {
            tier(11).colors(0xEBEBEB, 0x9B9B9B, 0xEBEBEB)
            ingot()
        }
        antimony = Material.create(102, clayiumId("antimony")) {
            tier(6).colors(0x464646, 0x191919, 0xFFFFFF)
            ingot()
        }
        barium = Material.create(103, clayiumId("barium")) {
            tier(7).colors(0x965078, 0x781450, 0xFFFFFF)
            ingot().dust()
            impureDust(0x965078, 0x78783C, 0xDCDCDC)
        }
        beryllium = Material.create(104, clayiumId("beryllium")) {
            tier(9).colors(0xD2F0D2, 0x191919, 0xFFFFFF)
            ingot().dust()
            impureDust(0xD2F0D2, 0x78783C, 0xDCDCDC)
        }
        bismuth = Material.create(105, clayiumId("bismuth")) {
            tier(9).colors(0x467846, 0x191919, 0xFFFFFF)
            ingot()
        }
        caesium = Material.create(106, clayiumId("caesium")) {
            tier(8).colors(0xF5F5F5, 0x969600, 0xFFFFFF)
            ingot()
        }
        calcium = Material.create(107, clayiumId("calcium")) {
            tier(7).colors(0xF0F0F0, 0x191919, 0xFFFFFF)
            ingot().dust()
            impureDust(0xF0F0F0, 0x78783C, 0xDCDCDC)
        }
        cerium = Material.create(108, clayiumId("cerium")) {
            tier(8).colors(0x919191, 0x969600, 0xFFFFFF)
            ingot()
        }
        chromium = Material.create(109, clayiumId("chrome")) {
            tier(9).colors(0xF0D2D2, 0x191919, 0xFFFFFF)
            ingot().dust()
        }
        cobalt = Material.create(110, clayiumId("cobalt")) {
            tier(8).colors(0x1E1EE6, 0x191919, 0xFFFFFF)
            ingot()
        }
        copper = Material.create(111, clayiumId("copper")) {
            tier(8).colors(0xA05A0A, 0x191919, 0xFFFFFF)
            ingot().dust()
            impureDust(0xA05A0A, 0x78783C, 0xDCDCDC)
        }
        curium = Material.create(112, clayiumId("curium")) {
            tier(12).colors(0xFFFFFF, 0x9B9B9B, 0xF4F4F4)
            ingot()
        }
        europium = Material.create(113, clayiumId("europium")) {
            tier(12).colors(0x919191, 0x373737, 0x919191)
            ingot()
        }
        francium = Material.create(114, clayiumId("francium")) {
            tier(8).colors(0xF5F5F5, 0x00EB00, 0xFFFFFF)
            ingot()
        }
        hafnium = Material.create(115, clayiumId("hafnium")) {
            tier(7).colors(0xF0D2AA, 0x191919, 0xFFFFFF)
            ingot().dust()
            impureDust(0xF0D2AA, 0x78783C, 0xDCDCDC)
        }
        iridium = Material.create(116, clayiumId("iridium")) {
            tier(11).colors(0xF0F0F0, 0xD2D2D2, 0xEBEBEB)
            ingot()
        }
        lanthanum = Material.create(117, clayiumId("lanthanum")) {
            tier(8).colors(0x919191, 0xEB0000, 0xFFFFFF)
            ingot()
        }
        lead = Material.create(118, clayiumId("lead")) {
            tier(8).colors(0xBEF0D2, 0x191919, 0xFFFFFF)
            ingot().dust()
            impureDust(0xBEF0D2, 0x78783C, 0xDCDCDC)
        }
        lithium = Material.create(119, clayiumId("lithium")) {
            tier(6).colors(0xD2D296, 0x787878, 0xFFFFFF)
            ingot()
            impureDust(0xDCDC96, 0x78783C, 0xDCDCDC)
        }
        magnesium = Material.create(120, clayiumId("magnesium")) {
            tier(6).colors(0x96D296, 0x787878, 0xFFFFFF)
            ingot().dust()
            impureDust(0x96DC96, 0x78783C, 0xDCDCDC)
        }
        manganese = Material.create(121, clayiumId("manganese")) {
            tier(7).colors(0xBEF0F0, 0x191919, 0xFFFFFF)
            ingot().dust()
            impureDust(0xBEF0F0, 0x78783C, 0xDCDCDC)
        }
        molybdenum = Material.create(122, clayiumId("molybdenum")) {
            tier(10).colors(0x82A082, 0x191919, 0xFFFFFF)
            ingot()
        }
        neodymium = Material.create(123, clayiumId("neodymium")) {
            tier(9).colors(0x919191, 0x009696, 0xFFFFFF)
            ingot()
        }
        neptunium = Material.create(124, clayiumId("neptunium")) {
            tier(9).colors(0x3232FF, 0x32329B, 0x3232FF)
            ingot()
        }
        osmium = Material.create(125, clayiumId("osmium")) {
            tier(11).colors(0x464696, 0x191919, 0xFFFFFF)
            ingot()
        }
        palladium = Material.create(126, clayiumId("palladium")) {
            tier(9).colors(0x974646, 0x191919, 0xFFFFFF)
            ingot()
        }
        platinum = Material.create(127, clayiumId("platinum")) {
            tier(10).colors(0xF5F5E6, 0x8C8C78, 0xFFFFFF)
            ingot()
        }
        plutonium = Material.create(128, clayiumId("plutonium")) {
            tier(10).colors(0xFF3232, 0x9B3232, 0xFF3232)
            ingot()
        }
        potassium = Material.create(129, clayiumId("potassium")) {
            tier(7).colors(0xF0F0BE, 0x191919, 0xFFFFFF)
            ingot()
            impureDust(0xAAAAE6, 0x78783C, 0xDCDCDC)
        }
        praseodymium = Material.create(130, clayiumId("praseodymium")) {
            tier(8).colors(0x919191, 0x00EB00, 0xFFFFFF)
            ingot()
        }
        promethium = Material.create(131, clayiumId("promethium")) {
            tier(10).colors(0x919191, 0x0000EB, 0xFFFFFF)
            ingot()
        }
        protactinium = Material.create(132, clayiumId("protactinium")) {
            tier(9).colors(0x323232, 0x191919, 0x323264)
            ingot()
        }
        radium = Material.create(133, clayiumId("radium")) {
            tier(8).colors(0xF5F5F5, 0x009696, 0xFFFFFF)
            ingot()
        }
        rhenium = Material.create(134, clayiumId("rhenium")) {
            tier(12).colors(0x464696, 0x191919, 0x32325A)
            ingot()
        }
        rubidium = Material.create(135, clayiumId("rubidium")) {
            tier(8).colors(0xF5F5F5, 0xEB0000, 0xFFFFFF)
            ingot()
        }
        samarium = Material.create(136, clayiumId("samarium")) {
            tier(11).colors(0x919191, 0x960096, 0xFFFFFF)
            ingot()
        }
        silver = Material.create(137, clayiumId("silver")) {
            tier(9).colors(0xE6E6F5, 0x78788C, 0xFFFFFF)
            ingot()
        }
        sodium = Material.create(138, clayiumId("sodium")) {
            tier(6).colors(0xAAAADE, 0x787878, 0xFFFFFF)
            ingot()
            impureDust(0xAAAAE6, 0x78783C, 0xDCDCDC)
        }
        strontium = Material.create(139, clayiumId("strontium")) {
            tier(7).colors(0xD2AAF2, 0x191919, 0xFFFFFF)
            ingot()
            impureDust(0xD2AAF2, 0x78783C, 0xDCDCDC)
        }
        tantalum = Material.create(140, clayiumId("tantalum")) {
            tier(8).colors(0xF0D2AA, 0x191919, 0xF0D296)
            ingot()
        }
        thorium = Material.create(141, clayiumId("thorium")) {
            tier(9).colors(0x323232, 0x191919, 0xC83232)
            ingot()
        }
        tin = Material.create(142, clayiumId("tin")) {
            tier(7).colors(0xE6E6F0, 0x000000, 0xFFFFFF)
            ingot()
        }
        titanium = Material.create(143, clayiumId("titanium")) {
            tier(8).colors(0xD2F0F0, 0x191919, 0xFFFFFF)
            ingot().dust()
            impureDust(0xD2F0F0, 0x78783C, 0xDCDCDC)
        }
        tungsten = Material.create(144, clayiumId("tungsten")) {
            tier(9).colors(0x1E1E1E, 0x191919, 0xFFFFFF)
            ingot()
        }
        uranium = Material.create(145, clayiumId("uranium")) {
            tier(9).colors(0x32FF32, 0x329B32, 0x32FF32)
            ingot()
        }
        vanadium = Material.create(146, clayiumId("vanadium")) {
            tier(9).colors(0x3C7878, 0x191919, 0xFFFFFF)
            ingot()
        }
        zinc = Material.create(147, clayiumId("zinc")) {
            tier(6).colors(0xE6AAAA, 0x787878, 0xFFFFFF)
            ingot().dust()
            impureDust(0xE6AAAA, 0x78783C, 0xDCDCDC)
        }
        zirconium = Material.create(148, clayiumId("zirconium")) {
            tier(6).colors(0xBEAA7A, 0x787878, 0xFFFFFF)
            ingot().dust()
            impureDust(0xBEAA7A, 0x78783C, 0xDCDCDC)
        }
        //endregion

        //region Others
        brass = Material.create(300, clayiumId("brass")) {
            tier(6).colors(0xBEAA14, 0x000000, 0xFFFFFF)
            ingot().dust()
        }
        bronze = Material.create(301, clayiumId("bronze")) {
            tier(5).colors(0xFA9628, 0x000000, 0xFFFFFF)
            ingot().dust()
        }
        electrum = Material.create(302, clayiumId("electrum")) {
            tier(6).colors(0xE6E69B, 0x787846, 0xFFFFFF)
            ingot().dust()
        }
        invar = Material.create(303, clayiumId("invar")) {
            tier(6).colors(0xAAAA50, 0x8C8C46, 0xB4B450)
            ingot().dust()
        }
        nickel = Material.create(304, clayiumId("nickel")) {
            tier(8).colors(0xD2D2F0, 0x191919, 0xFFFFFF)
            ingot().dust()
            impureDust(0xD2D2F0, 0x78783C, 0xDCDCDC)
        }
        steel = Material.create(305, clayiumId("steel")) {
            tier(3).colors(0x5A5A6E, 0x000000, 0xFFFFFF)
            ingot().dust()
        }
        zinc_aluminum = Material.create(306, clayiumId("zinc_aluminum")) {
            tier(6).colors(0xF0BEDC, 0xA00000, 0xFFFFFF)
            ingot().dust()
        }
        zinc_zirconium = Material.create(307, clayiumId("zinc_zirconium")) {
            tier(6).colors(0xE6AA8C, 0x780000, 0xFFFFFF)
            ingot().dust()
        }
        //endregion

        salt = Material.create(1012, clayiumId("salt")) {
            tier(4)
            colors(0xFFFFFF, 0x8C8C8C, 0xFFFFFF)
            dust()
        }

        organicClay = Material.create(1013, clayiumId("organic_clay")) {
            tier(5)
            colors(0x8890AD, 0x6A2C2B, 0x92A4B7)
            dust()
        }
    }

    fun init() {}
}