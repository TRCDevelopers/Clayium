package com.github.trcdevelopers.clayium.common.items

import java.util.Locale

/**
 * This enum manages the material's tier, name, color, and ore dictionary.
 * This makes it easier to manage materials that has many forms (dust, ingot, etc.).
 */
enum class ColoredMaterial(
    override val tier: Int = -1,
    override val materialName: String,
    override vararg val colors: Int,
) : IMaterial {

    SILICONE(5, "silicone", 0xD2D2D2, 0xB4B4B4, 0xF0F0F0),
    SILICON(5, "silicon", 0x281C28, 0x191919, 0xFFFFFF),
    ALUMINUM(6, "aluminum", 0xBEC8CA, 0x191919, 0xFFFFFF),
    CLAY_STEEL(7, "clay_steel", 0x8890AD, 0x191919, 0xFFFFFF),
    CLAYIUM(8, "clayium", 0x5AF0D2, 0x3F4855, 0xFFCDC8),
    ULTIMATE_ALLOY(9, "ultimate_alloy", 0x55CD55, 0x191919, 0xF5A0FF),
    ANTIMATTER(10, "antimatter", 0x0000EB, 0x000000, 0xFFFFFF),
    PURE_ANTIMATTER_TIER0(11, "pure_antimatter", 0xFF32FF, 0x000000, 0xFFFFFF),
    OCTUPLE_ENERGETIC_CLAY(12, "oec", 0xFFFF00, 0x8C8C8C, 0xFFFFFF),
    PURE_ANTIMATTER_TIER8(13, "opa", 0x960000, 0xC8C800, 0xFFFFFF),

    AZ91D(6, "az91d", 0x828C87, 0x0A280A, 0xFFFFFF),
    ZK60A(6, "zk60a", 0x4B5550, 0x0A280A, 0xFFFFFF),

    BARIUM("barium", 0x965078, 0x781450, 0xFFFFFF),
    BERYLLIUM("beryllium", 0xD2F0D2, 0x191919, 0xFFFFFF),
    BRASS("brass", 0xBEAA14, 0x000000, 0xFFFFFF),
    BRONZE("bronze", 0xFA9628, 0x000000, 0xFFFFFF),
    CALCIUM("calcium", 0xF0F0F0, 0x191919, 0xFFFFFF),
    CHROME("chrome", 0xF0D2D2, 0x191919, 0xFFFFFF),
    COPPER("copper", 0xA05A0A, 0x191919, 0xFFFFFF),
    ELECTRUM("electrum", 0xE6E69B, 0x787846, 0xFFFFFF),
    HAFNIUM("hafnium", 0xF0D2AA, 0x191919, 0xFFFFFF),
    INVAR("invar", 0xAAAA50, 0x8C8C46, 0xB4B450),
    LEAD("lead", 0xBEF0D2, 0x191919, 0xFFFFFF),
    LITHIUM("lithium", 0xD2D296, 0x787878, 0xFFFFFF),
    MANGANESE("manganese", 0xBEF0F0, 0x191919, 0xFFFFFF),
    MAGNESIUM("magnesium", 0x96D296, 0x787878, 0xFFFFFF),
    NICKEL("nickel", 0xD2D2F0, 0x191919, 0xFFFFFF),
    POTASSIUM("potassium", 0xF0F0BE, 0x191919, 0xFFFFFF),
    SODIUM("sodium", 0xAAAADE, 0x787878, 0xFFFFFF),
    STEEL("steel", 0x5A5A6E, 0x000000, 0xFFFFFF),
    STRONTIUM("strontium", 0xD2AAF2, 0x191919, 0xFFFFFF),
    TITANIUM("titanium", 0xD2F0F0, 0x191919, 0xFFFFFF),
    ZINC("zinc", 0xE6AAAA, 0x787878, 0xFFFFFF),
    ZIRCONIUM("zirconium", 0xBEAA7A, 0x787878, 0xFFFFFF),

    ACTINIUM("actinium", 0xF5F5F5, 0x0000EB, 0xFFFFFF),
    AMERICIUM("americium", 0xEBEBEB, 0x9B9B9B, 0xEBEBEB),
    ANTIMONY("antimony", 0x464646, 0x191919, 0xFFFFFF),
    BISMUTH("bismuth", 0x467846, 0x191919, 0xFFFFFF),
    CAESIUM("caesium", 0xF5F5F5, 0x969600, 0xFFFFFF),
    CERIUM("cerium", 0x919191, 0x969600, 0xFFFFFF),
    COBALT("cobalt", 0x1E1EE6, 0x191919, 0xFFFFFF),
    CURIUM("curium", 0xFFFFFF, 0x9B9B9B, 0xF4F4F4),
    EUROPIUM("europium", 0x919191, 0x373737, 0x919191),
    FRANCIUM("francium", 0xF5F5F5, 0x00EB00, 0xFFFFFF),
    IRIDIUM("iridium", 0xF0F0F0, 0xD2D2D2, 0xEBEBEB),
    LANTHANUM("lanthanum", 0x919191, 0xEB0000, 0xFFFFFF),
    MOLYBDENUM("molybdenum", 0x82A082, 0x191919, 0xFFFFFF),
    NEODYMIUM("neodymium", 0x919191, 0x009696, 0xFFFFFF),
    NEPTUNIUM("neptunium", 0x3232FF, 0x32329B, 0x3232FF),
    OSMIUM("osmium", 0x464696, 0x191919, 0xFFFFFF),
    PALLADIUM("palladium", 0x974646, 0x191919, 0xFFFFFF),
    PLATINUM("platinum", 0xF5F5E6, 0x8C8C78, 0xFFFFFF),
    PLUTONIUM("plutonium", 0xFF3232, 0x9B3232, 0xFF3232),
    PRASEODYMIUM("praseodymium", 0x919191, 0x00EB00, 0xFFFFFF),
    PROMETHIUM("promethium", 0x919191, 0x0000EB, 0xFFFFFF),
    PROTACTINIUM("protactinium", 0x323232, 0x191919, 0x323264),
    RADIUM("radium", 0xF5F5F5, 0x009696, 0xFFFFFF),
    RHENIUM("rhenium", 0x464696, 0x191919, 0x32325A),
    RUBIDIUM("rubidium", 0xF5F5F5, 0xEB0000, 0xFFFFFF),
    SAMARIUM("samarium", 0x919191, 0x960096, 0xFFFFFF),
    SILVER("silver", 0xE6E6F5, 0x78788C, 0xFFFFFF),
    TANTALUM("tantalum", 0xF0D2AA, 0x191919, 0xF0D296),
    THORIUM("thorium", 0x323232, 0x191919, 0xC83232),
    TIN("tin", 0xE6E6F0, 0x000000, 0xFFFFFF),
    TUNGSTEN("tungsten", 0x1E1E1E, 0x191919, 0xFFFFFF),
    URANIUM("uranium", 0x32FF32, 0x329B32, 0x32FF32),
    VANADIUM("vanadium", 0x3C7878, 0x191919, 0xFFFFFF),
    ZINC_ALUMINUM("zinc_aluminum", 0xF0BEDC, 0xA00000, 0xFFFFFF),
    ZINC_ZIRCONIUM("zinc_zirconium", 0xE6AA8C, 0x780000, 0xFFFFFF),
    ;

    override val oreDictSuffix = materialName.split("_").joinToString(separator = "") {s ->
        s.replaceFirstChar { c -> c.titlecase(Locale.ROOT) }
    }

    init {
        require(materialName.matches(Regex("[0-9a-z_]+"))) {
            "Material name must be lower snake case"
        }
    }

    constructor(materialName: String, vararg color: Int) : this(-1, materialName, *color)
}