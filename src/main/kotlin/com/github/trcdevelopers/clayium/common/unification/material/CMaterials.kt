package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.api.util.clayiumId
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.unification.material.MaterialFlags.GENERATE_CLAY_PARTS

@Suppress("unused")
object CMaterials {

    /** 1 - 150 */
    //region Element Materials
    val actinium = Material.create(1, clayiumId("actinium")) {
        tier(7).colors(0x8E1777, 0x323200, 0xFFFFFF)
        ingot().dust()
        impureDust(0x8E1777, 0x78783C, 0xDCDCDC)
    }
    val aluminum = Material.create(2, clayiumId("aluminum")) {
        tier(6)
        colors(0xBEC8CA, 0x191919, 0xFFFFFF)
        ingot().dust().impureDust(0xBEC8CA, 0x78783C, 0xDCDCDC)
        plate(ClayEnergy.milli(1), 20, tier = 4)
        claySmelting(0.5, 5, 200)
    }
    val americium = Material.create(3, clayiumId("americium")) {
        tier(11).colors(0xEBEBEB, 0x9B9B9B, 0xEBEBEB)
        ingot()
    }
    val antimony = Material.create(4, clayiumId("antimony")) {
        tier(6).colors(0x464646, 0x191919, 0xFFFFFF)
        ingot()
    }

    val argon = Material.create(5, clayiumId("argon")) {}
    val arsenic = Material.create(6, clayiumId("arsenic")) {}
    val astatine = Material.create(7, clayiumId("astatine")) {}
    val barium = Material.create(8, clayiumId("barium")) {
        tier(7).colors(0x965078, 0x781450, 0xFFFFFF)
        ingot().dust()
        impureDust(0x965078, 0x78783C, 0xDCDCDC)
        blastSmelting(2.0, 7, 1000)
    }
    val berkelium = Material.create(9, clayiumId("berkelium")) {}
    val beryllium = Material.create(10, clayiumId("beryllium")) {
        tier(9).colors(0xD2F0D2, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xD2F0D2, 0x78783C, 0xDCDCDC)
        blastSmelting(6, 500)
    }
    val bismuth = Material.create(11, clayiumId("bismuth")) {
        tier(9).colors(0x467846, 0x191919, 0xFFFFFF)
        ingot()
    }
    val bohrium = Material.create(12, clayiumId("bohrium")) {}
    val boron = Material.Builder(13, clayiumId("boron")).build()
    val bromine = Material.Builder(14, clayiumId("bromine")).build()
    val caesium = Material.create(15, clayiumId("caesium")) {
        tier(8).colors(0xF5F5F5, 0x969600, 0xFFFFFF)
        ingot()
    }
    val calcium = Material.create(16, clayiumId("calcium")) {
        tier(7).colors(0xF0F0F0, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xF0F0F0, 0x78783C, 0xDCDCDC)
        blastSmelting(5, 500)
    }
    val californium = Material.create(17, clayiumId("californium")) {}
    val carbon = Material.create(18, clayiumId("carbon")) {
        tier(6).colors(0x0A0A0A, 0x191919, 0x1E1E1E)
        ingot().dust()
    }
    val cadmium = Material.create(19, clayiumId("cadmium")) {}
    val cerium = Material.create(20, clayiumId("cerium")) {
        tier(8).colors(0x919191, 0x969600, 0xFFFFFF)
        ingot()
    }
    val chlorine = Material.create(21, clayiumId("chlorine")) {}
    val chromium = Material.create(22, clayiumId("chrome")) {
        tier(9).colors(0xF0D2D2, 0x191919, 0xFFFFFF)
        ingot().dust()
        blastSmelting(4.0, 9, 2000)
    }
    val cobalt = Material.create(23, clayiumId("cobalt")) {
        tier(8).colors(0x1E1EE6, 0x191919, 0xFFFFFF)
        ingot()
    }
    val copernicium = Material.create(24, clayiumId("copernicium")) {}
    val copper = Material.create(25, clayiumId("copper")) {
        tier(8).colors(0xA05A0A, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xA05A0A, 0x78783C, 0xDCDCDC)
    }
    val curium = Material.create(26, clayiumId("curium")) {
        tier(12).colors(0xFFFFFF, 0x9B9B9B, 0xF4F4F4)
        ingot()
    }
    val darmstadtium = Material.create(27, clayiumId("darmstadtium")) {}
    val dubnium = Material.create(28, clayiumId("dubnium")) {}
    val dysprosium = Material.create(29, clayiumId("dysprosium")) {}
    val einsteinium = Material.create(30, clayiumId("einsteinium")) {}
    val erbium = Material.create(31, clayiumId("erbium")) {}
    val europium = Material.create(32, clayiumId("europium")) {
        tier(12).colors(0x919191, 0x373737, 0x919191)
        ingot()
    }
    val fermium = Material.create(33, clayiumId("fermium")) {}
    val flerovium = Material.create(34, clayiumId("flerovium")) {}
    val fluorine = Material.create(35, clayiumId("fluorine")) {}
    val francium = Material.create(36, clayiumId("francium")) {
        tier(8).colors(0xF5F5F5, 0x00EB00, 0xFFFFFF)
        ingot()
    }
    val gadolinium = Material.create(37, clayiumId("gadolinium")) {}
    val gallium = Material.create(38, clayiumId("gallium")) {}
    val germanium = Material.create(39, clayiumId("germanium")) {}
    val gold = Material.create(40, clayiumId("gold")) {}
    val hafnium = Material.create(41, clayiumId("hafnium")) {
        tier(7).colors(0xF0D2AA, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xF0D2AA, 0x78783C, 0xDCDCDC)
        blastSmelting(6, 500)
    }
    val hassium = Material.create(42, clayiumId("hassium")) {}
    val helium = Material.create(43, clayiumId("helium")) {}
    val holmium = Material.create(44, clayiumId("holmium")) {}
    val hydrogen = Material.create(45, clayiumId("hydrogen")) {}
    val indium = Material.create(46, clayiumId("indium")) {}
    val iodine = Material.create(47, clayiumId("iodine")) {}
    val iridium = Material.create(48, clayiumId("iridium")) {
        tier(11).colors(0xF0F0F0, 0xD2D2D2, 0xEBEBEB)
        ingot()
    }
    val iron = Material.create(49, clayiumId("iron")) {}
    val krypton = Material.create(50, clayiumId("krypton")) {}
    val lanthanum = Material.create(51, clayiumId("lanthanum")) {
        tier(8).colors(0x919191, 0xEB0000, 0xFFFFFF)
        ingot()
    }
    val lawrencium = Material.create(52, clayiumId("lawrencium")) {}
    val lead = Material.create(53, clayiumId("lead")) {
        tier(8).colors(0xBEF0D2, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xBEF0D2, 0x78783C, 0xDCDCDC)
    }
    val lithium = Material.create(54, clayiumId("lithium")) {
        tier(6).colors(0xD2D296, 0x787878, 0xFFFFFF)
        ingot().dust()
        impureDust(0xDCDC96, 0x78783C, 0xDCDCDC)
    }
    val livermorium = Material.create(55, clayiumId("livermorium")) {}
    val lutetium = Material.create(56, clayiumId("lutetium")) {}
    val magnesium = Material.create(57, clayiumId("magnesium")) {
        tier(6).colors(0x96D296, 0x787878, 0xFFFFFF)
        ingot().dust()
        impureDust(0x96DC96, 0x78783C, 0xDCDCDC)
        claySmelting(0.2, 6, 400)
    }
    val manganese = Material.create(58, clayiumId("manganese")) {
        tier(7).colors(0xBEF0F0, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xBEF0F0, 0x78783C, 0xDCDCDC)
        blastSmelting(2.0, 7, 1000)
    }
    val meitnerium = Material.create(59, clayiumId("meitnerium")) {}
    val mendelevium = Material.create(60, clayiumId("mendelevium")) {}
    val mercury = Material.create(61, clayiumId("mercury")) {}
    val molybdenum = Material.create(62, clayiumId("molybdenum")) {
        tier(10).colors(0x82A082, 0x191919, 0xFFFFFF)
        ingot()
    }
    val moscovium = Material.create(63, clayiumId("moscovium")) {}
    val neodymium = Material.create(64, clayiumId("neodymium")) {
        tier(9).colors(0x919191, 0x009696, 0xFFFFFF)
        ingot()
    }
    val neon = Material.create(65, clayiumId("neon")) {}
    val neptunium = Material.create(66, clayiumId("neptunium")) {
        tier(9).colors(0x3232FF, 0x32329B, 0x3232FF)
        ingot()
    }
    val nickel = Material.create(67, clayiumId("nickel")) {
        tier(8).colors(0xD2D2F0, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xD2D2F0, 0x78783C, 0xDCDCDC)
        claySmelting(0.5, 5, 200)
    }
    val nihonium = Material.create(68, clayiumId("nihonium")) {}
    val niobium = Material.create(69, clayiumId("niobium")) {}
    val nitrogen = Material.create(70, clayiumId("nitrogen")) {}
    val nobelium = Material.create(71, clayiumId("nobelium")) {}
    val oganesson = Material.create(72, clayiumId("oganesson")) {}
    val osmium = Material.create(73, clayiumId("osmium")) {
        tier(11).colors(0x464696, 0x191919, 0xFFFFFF)
        ingot()
    }
    val oxygen = Material.create(74, clayiumId("oxygen")) {}
    val palladium = Material.create(75, clayiumId("palladium")) {
        tier(9).colors(0x974646, 0x191919, 0xFFFFFF)
        ingot()
    }
    val phosphorus = Material.create(76, clayiumId("phosphorus")) {
        tier(6).colors(0xB4B419, 0x9B9B00, 0xCDCD32)
        dust()
    }
    val platinum = Material.create(77, clayiumId("platinum")) {
        tier(10).colors(0xF5F5E6, 0x8C8C78, 0xFFFFFF)
        ingot()
    }
    val plutonium = Material.create(78, clayiumId("plutonium")) {
        tier(10).colors(0xFF3232, 0x9B3232, 0xFF3232)
        ingot()
    }
    val polonium = Material.create(79, clayiumId("polonium")) {}
    val potassium = Material.create(80, clayiumId("potassium")) {
        tier(7).colors(0xF0F0BE, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xAAAAE6, 0x78783C, 0xDCDCDC)
        blastSmelting(5, 500)
    }
    val praseodymium = Material.create(81, clayiumId("praseodymium")) {
        tier(8).colors(0x919191, 0x00EB00, 0xFFFFFF)
        ingot()
    }
    val promethium = Material.create(82, clayiumId("promethium")) {
        tier(10).colors(0x919191, 0x0000EB, 0xFFFFFF)
        ingot()
    }
    val protactinium = Material.create(83, clayiumId("protactinium")) {
        tier(9).colors(0x323232, 0x191919, 0x323264)
        ingot()
    }
    val radium = Material.create(84, clayiumId("radium")) {
        tier(8).colors(0xF5F5F5, 0x009696, 0xFFFFFF)
        ingot()
    }
    val radon = Material.create(85, clayiumId("radon")) {}
    val rhenium = Material.create(86, clayiumId("rhenium")) {
        tier(12).colors(0x464696, 0x191919, 0x32325A)
        ingot()
    }
    val rhodium = Material.create(87, clayiumId("rhodium")) {}
    val roentgenium = Material.create(88, clayiumId("roentgenium")) {}
    val rubidium = Material.create(89, clayiumId("rubidium")) {
        tier(8).colors(0xF5F5F5, 0xEB0000, 0xFFFFFF)
        ingot()
    }
    val ruthenium = Material.create(90, clayiumId("ruthenium")) {}
    val rutherfordium = Material.create(91, clayiumId("rutherfordium")) {}
    val samarium = Material.create(92, clayiumId("samarium")) {
        tier(11).colors(0x919191, 0x960096, 0xFFFFFF)
        ingot()
    }
    val scandium = Material.create(93, clayiumId("scandium")) {}
    val seaborgium = Material.create(94, clayiumId("seaborgium")) {}
    val selenium = Material.create(95, clayiumId("selenium")) {}
    val silicon = Material.create(96, clayiumId("silicon")) {
        tier(5)
        colors(0x281C28, 0x191919, 0xFFFFFF)
        ingot().dust()
        plate(ClayEnergy.milli(1), 20, tier = 4)
    }
    val silver = Material.create(97, clayiumId("silver")) {
        tier(9).colors(0xE6E6F5, 0x78788C, 0xFFFFFF)
        ingot()
    }
    val sodium = Material.create(98, clayiumId("sodium")) {
        tier(6).colors(0xAAAADE, 0x787878, 0xFFFFFF)
        ingot().dust()
        impureDust(0xAAAAE6, 0x78783C, 0xDCDCDC)
        claySmelting(0.5, 5, 200)
    }
    val strontium = Material.create(99, clayiumId("strontium")) {
        tier(7).colors(0xD2AAF2, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xD2AAF2, 0x78783C, 0xDCDCDC)
        blastSmelting(2.0, 7, 1000)
    }
    val sulfur = Material.create(100, clayiumId("sulfur")) {
        tier(6).colors(0xE6E600, 0xCDCD00, 0xFFFF00)
        dust()
    }
    val tantalum = Material.create(101, clayiumId("tantalum")) {
        tier(8).colors(0xF0D2AA, 0x191919, 0xF0D296)
        ingot()
    }
    val technetium = Material.create(102, clayiumId("technetium")) {}
    val tellurium = Material.create(103, clayiumId("tellurium")) {}
    val tennessine = Material.create(104, clayiumId("tennessine")) {}
    val terbium = Material.create(105, clayiumId("terbium")) {}
    val thallium = Material.create(106, clayiumId("thallium")) {}
    val thorium = Material.create(107, clayiumId("thorium")) {
        tier(9).colors(0x323232, 0x191919, 0xC83232)
        ingot()
    }
    val thulium = Material.create(108, clayiumId("thulium")) {}
    val tin = Material.create(109, clayiumId("tin")) {
        tier(7).colors(0xE6E6F0, 0x000000, 0xFFFFFF)
        ingot()
    }
    val titanium = Material.create(110, clayiumId("titanium")) {
        tier(8).colors(0xD2F0F0, 0x191919, 0xFFFFFF)
        ingot().dust()
        impureDust(0xD2F0F0, 0x78783C, 0xDCDCDC)
        blastSmelting(4.0, 8, 2000)
    }
    val tungsten = Material.create(111, clayiumId("tungsten")) {
        tier(9).colors(0x1E1E1E, 0x191919, 0xFFFFFF)
        ingot()
    }
    val uranium = Material.create(112, clayiumId("uranium")) {
        tier(9).colors(0x32FF32, 0x329B32, 0x32FF32)
        ingot()
    }
    val vanadium = Material.create(113, clayiumId("vanadium")) {
        tier(9).colors(0x3C7878, 0x191919, 0xFFFFFF)
        ingot()
    }
    val xenon = Material.create(114, clayiumId("xenon")) {}
    val ytterbium = Material.create(115, clayiumId("ytterbium")) {}
    val yttrium = Material.create(116, clayiumId("yttrium")) {}
    val zinc = Material.create(117, clayiumId("zinc")) {
        tier(6).colors(0xE6AAAA, 0x787878, 0xFFFFFF)
        ingot().dust()
        impureDust(0xE6AAAA, 0x78783C, 0xDCDCDC)
        claySmelting(0.5, 5, 200)
    }
    val zirconium = Material.create(118, clayiumId("zirconium")) {
        tier(6).colors(0xBEAA7A, 0x787878, 0xFFFFFF)
        ingot().dust()
        impureDust(0xBEAA7A, 0x78783C, 0xDCDCDC)
        claySmelting(0.2, 6, 400)
    }
    //endregion

    /** 151 - 200 */
    //region Clay Materials
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
    val octupleEnergyClay: Material = Material.create(164, clayiumId("octuple_energy_clay")) {
        tier(12)
        colors(0xFFFF00, 0x8C8C8C, 0xFFFFFF)
        dust()
        clay(energy = ClayEnergy.of(100_000_000))
        plate(ClayEnergy.of(10000), 20, tier = 9)
    }
    //endregion

    /** 201 - 300 */
    //region Matters
    val antimatter = Material.create(201, clayiumId("antimatter")) {
        tier(10)
        colors(0x0000EB, 0x000000, 0xFFFFFF)
        matter().dust()
        plate(ClayEnergy.of(100), 20, tier = 9)
    }
    val pureAntimatter = Material.create(202, clayiumId("pure_antimatter")) {
        tier(11)
        colors(0xFF32FF, 0x000000, 0xFFFFFF)
        matter().dust()
        plate(ClayEnergy.of(100), 20, tier = 9)
    }
    val pureAntimatter1 = Material.create(203, clayiumId("pure_antimatter1")) {
        tier(11).colors(0xC42385, 0x191919, 0xFFFFFF)
        matter("matter2")
    }
    val pureAntimatter2 = Material.create(204, clayiumId("pure_antimatter2")) {
        tier(11).colors(0x8E1777, 0x323200, 0xFFFFFF)
        matter("matter2")
    }
    val pureAntimatter3 = Material.create(205, clayiumId("pure_antimatter3")) {
        tier(11).colors(0x5E0D45, 0x4B4B00, 0xFFFFFF)
        matter("matter3")
    }
    val pureAntimatter4 = Material.create(206, clayiumId("pure_antimatter4")) {
        tier(12).colors(0x32061F, 0x646400, 0xFFFFFF)
        matter("matter3")
    }
    val pureAntimatter5 = Material.create(207, clayiumId("pure_antimatter5")) {
        tier(12).colors(0x520829, 0x7D7D00, 0xFFFFFF)
        matter("matter4")
    }
    val pureAntimatter6 = Material.create(208, clayiumId("pure_antimatter6")) {
        tier(12).colors(0x6E0727, 0x969600, 0xFFFFFF)
        matter("matter4")
    }
    val pureAntimatter7 = Material.create(209, clayiumId("pure_antimatter7")) {
        tier(13).colors(0x840519, 0xAFAF00, 0xFFFFFF)
        matter("matter4")
    }
    val octuplePureAntimatter = Material.create(210, clayiumId("octuple_pure_antimatter")) {
        tier(13).colors(0x960000, 0xC8C800, 0xFFFFFF)
        matter("matter5").dust()
        plate(ClayEnergy.of(100), 20, tier = 9)
    }
    //endregion

    /** 301 - 500 */
    //region Chemical Materials
    val organicClay = Material.create(301, clayiumId("organic_clay")) {
        tier(5)
        colors(0x8890AD, 0x6A2C2B, 0x92A4B7)
        dust()
    }
    val calciumChloride = Material.create(302, clayiumId("calcium_chloride")) {
        dust()
    }
    val sodiumCarbonate = Material.create(303, clayiumId("sodium_carbonate")) {
        dust()
    }
    val calcareousClay = Material.create(304, clayiumId("calcareous_clay")) {
        tier(4)
        dust()
    }
    //endregion

    /**
     * Materials composed of anything other than First or higher Degree Materials.
     * 501 - 1000
     */
    //region First Degree Materials
    val brass = Material.create(501, clayiumId("brass")) {
        tier(6).colors(0xBEAA14, 0x000000, 0xFFFFFF)
        ingot().dust()
    }
    val bronze = Material.create(502, clayiumId("bronze")) {
        tier(5).colors(0xFA9628, 0x000000, 0xFFFFFF)
        ingot().dust()
    }
    val charcoal = Material.create(503, clayiumId("charcoal")) {
        colors(0x141414, 0x191919, 0x503232)
        dust()
    }
    val claySteel = Material.create(504, clayiumId("clay_steel")) {
        tier(7)
        colors(0x8890AD, 0x191919, 0xFFFFFF)
        ingot().dust()
        plate(ClayEnergy.milli(1), 60, tier = 4)
        blastSmelting(6, 500)
    }
    val clayium = Material.create(505, clayiumId("clayium")) {
        tier(8)
        colors(0x5AF0D2, 0x3F4855, 0xFFCDC8)
        ingot().dust()
        plate(ClayEnergy.milli(1), 120, tier = 4)
        blastSmelting(2.0, 7, 1000)
    }
    val coal = Material.create(506, clayiumId("coal")) {
        colors(0x141414, 0x191919, 0x323250)
        dust()
    }
    val graphite = Material.create(507, clayiumId("graphite")) {}
    val electrum = Material.create(508, clayiumId("electrum")) {
        tier(6).colors(0xE6E69B, 0x787846, 0xFFFFFF)
        ingot().dust()
    }
    val impureGlowStone = Material.create(509, clayiumId("impure_glowstone")) {
        colors(0x979746, 0x191919, 0xFFFFFF)
        dust()
    }
    val impureRedstone = Material.create(510, clayiumId("impure_redstone")) {
        colors(0x974646, 0x191919, 0xFFFFFF)
        dust()
    }
    val impureSilicon = Material.create(511, clayiumId("impure_silicon")) {
        tier(5)
        colors(0x978F98, 0x533764, 0xA9A5A5)
        ingot().dust()
        plate(ClayEnergy.milli(1), 20, tier = 4)
    }
    val invar = Material.create(512, clayiumId("invar")) {
        tier(6).colors(0xAAAA50, 0x8C8C46, 0xB4B450)
        ingot().dust()
    }
    val lapis = Material.create(513, clayiumId("lapis")) {
        colors(0x3C64BE, 0x0A2B7A, 0x5A82E2)
        dust()
    }
    val quartz = Material.create(514, clayiumId("quartz")) {
        dust()
    }
    val salt = Material.create(515, clayiumId("salt")) {
        tier(4)
        colors(0xFFFFFF, 0x8C8C8C, 0xFFFFFF)
        dust()
    }
    val saltpeter = Material.create(516, clayiumId("saltpeter")) {
        colors(0xDEDCDC, 0xBEC8D2, 0xFFF0E6)
        dust()
    }
    val silicone = Material.create(517, clayiumId("silicone")) {
        tier(5)
        colors(0xD2D2D2, 0xB4B4B4, 0xF0F0F0)
        ingot().dust()
        plate(ClayEnergy.milli(1), 4, tier = 4)
    }
    val steel = Material.create(518, clayiumId("steel")) {
        tier(3).colors(0x5A5A6E, 0x000000, 0xFFFFFF)
        ingot().dust()
        blastSmelting(6, 500)
    }
    val ultimateAlloy = Material.create(519, clayiumId("ultimate_alloy")) {
        tier(9)
        colors(0x55CD55, 0x191919, 0xF5A0FF)
        ingot().dust()
        plate(ClayEnergy.milli(1), 180, tier = 4)
        blastSmelting(4.0, 8, 2000)
    }
    val zinc_aluminum = Material.create(520, clayiumId("zinc_aluminum")) {
        tier(6).colors(0xF0BEDC, 0xA00000, 0xFFFFFF)
        ingot().dust()
        claySmelting(0.2, 6, 400)
    }
    val zinc_zirconium = Material.create(521, clayiumId("zinc_zirconium")) {
        tier(6).colors(0xE6AA8C, 0x780000, 0xFFFFFF)
        ingot().dust()
        claySmelting(0.2, 6, 400)
    }
    //endregion

    /** 1001 - 1500 */
    //region Higher Degree Materials
    val az91d = Material.create(1001, clayiumId("az91d")) {
        tier(6).colors(0x828C87, 0x0A280A, 0xFFFFFF)
        ingot().dust()
        plate(ClayEnergy.milli(1), 20, tier = 4)
        claySmelting(0.2, 6, 400)
    }
    val zk60a = Material.create(1002, clayiumId("zk60a")) {
        tier(6).colors(0x4B5550, 0x0A280A, 0xFFFFFF)
        ingot().dust()
        plate(ClayEnergy.milli(1), 20, tier = 4)
        claySmelting(0.2, 6, 400)
    }
    val impureUltimateAlloy = Material.create(1003, clayiumId("impure_ultimate")) {
        tier(8).colors(0x55CD55, 0xF5FFFF, 0xF5A0FF)
        ingot()
    }
    //endregion

    // don't use builder so it is not registered
    val DUMMY = Material(0, clayiumId("dummy"), MaterialProperties())

    val PURE_ANTIMATTERS = listOf(pureAntimatter, pureAntimatter1, pureAntimatter2, pureAntimatter3,
        pureAntimatter4, pureAntimatter5, pureAntimatter6, pureAntimatter7, octuplePureAntimatter)

    init {
        //region Clay Materials
        compressedEnergeticClay7 = Material.create(163, clayiumId("compressed_energetic_clay7")) {
            tier(11)
            clay(octupleEnergyClay, ClayEnergy.of(10_000_000))
        }

        compressedEnergeticClay6 = Material.create(162, clayiumId("compressed_energetic_clay6")) {
            tier(10)
            clay(compressedEnergeticClay7, ClayEnergy.of(1_000_000))
        }

        compressedEnergeticClay5 = Material.create(161, clayiumId("compressed_energetic_clay5")) {
            tier(9)
            clay(compressedEnergeticClay6, ClayEnergy.of(100_000))
        }

        compressedEnergeticClay4 = Material.create(160, clayiumId("compressed_energetic_clay4")) {
            tier(8)
            clay(compressedEnergeticClay5, ClayEnergy.of(10_000))
        }

        compressedEnergeticClay3 = Material.create(159, clayiumId("compressed_energetic_clay3")) {
            tier(7)
            clay(compressedEnergeticClay4, ClayEnergy.of(1_000))
        }

        compressedEnergeticClay2 = Material.create(158, clayiumId("compressed_energetic_clay2")) {
            tier(6)
            clay(compressedEnergeticClay3, ClayEnergy.of(100))
        }

        compressedEnergeticClay = Material.create(157, clayiumId("compressed_energetic_clay")) {
            tier(5)
            clay(compressedEnergeticClay2, ClayEnergy.of(10))
        }

        energeticClay = Material.create(156, clayiumId("energetic_clay")) {
            tier(4)
            clay(compressedEnergeticClay, ClayEnergy.of(1))
        }

        advancedIndustrialClay = Material.create(155, clayiumId("advanced_industrial_clay")) {
            tier(3)
            clay(energeticClay)
            dust()
            plate(ClayEnergy.micro(40), 4, tier = 3)
        }

        industrialClay = Material.create(154, clayiumId("industrial_clay")) {
            tier(2)
            clay(advancedIndustrialClay)
            dust()
            plate(ClayEnergy.micro(20), 4, tier = 2)
        }

        compressedClay = Material.create(153, clayiumId("compressed_clay")) {
            tier(1)
            clay(industrialClay)
        }

        denseClay = Material.create(152, clayiumId("dense_clay")) {
            tier(0)
            clay(compressedClay)
            dust()
            plate(ClayEnergy.micro(10), 4, tier = 0)
            flags(GENERATE_CLAY_PARTS)
        }

        clay = Material.create(151, clayiumId("clay")) {
            tier(0)
            clay(denseClay)
            dust()
            plate(ClayEnergy.micro(10), 1, tier = 0)
            flags(GENERATE_CLAY_PARTS)
        }
        //endregion
    }

    fun init() {}
}