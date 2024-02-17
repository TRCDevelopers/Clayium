package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Dust
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.ImpureDust
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Ingot
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Matter
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Plate
import net.minecraft.util.IntIdentityHashBiMap

enum class Material(
    val uniqueId: Int,
    val materialName: String,
    val tier: Int = -1,
    val colors: IntArray? = null,
    properties: Set<MaterialProperty> = emptySet(),
) {

    // impure material but there is no impure -> pure dust recipe
    IMPURE_SILICON(0, "impure_silicon", 5,
        colors = intArrayOf(0x978F98, 0x533764, 0xA9A5A5),
        properties = setOf(Ingot, Dust, Plate(20))
    ),
    SILICONE(1, "silicone", 5,
        colors = intArrayOf(0xD2D2D2, 0xB4B4B4, 0xF0F0F0),
        properties = setOf(Ingot, Dust, Plate(20))
    ),
    SILICON(2, "silicon", 5,
        colors = intArrayOf(0x281C28, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, Plate(20))
    ),
    ALUMINUM(3, "aluminum", 6,
        colors =  intArrayOf(0xBEC8CA, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xBEC8CA, 0x78783C, 0xDCDCDC), Plate(20))
    ),
    CLAY_STEEL(4, "clay_steel", 7,
        colors =  intArrayOf(0x8890AD, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, Plate(20))
    ),
    CLAYIUM(5, "clayium", 8,
        colors =  intArrayOf(0x5AF0D2, 0x3F4855, 0xFFCDC8),
        properties = setOf(Ingot, Dust, Plate(20))
    ),
    ULTIMATE_ALLOY(6, "ultimate_alloy", 9,
        colors =  intArrayOf(0x55CD55, 0x191919, 0xF5A0FF),
        properties = setOf(Ingot, Dust, Plate(20))
    ),
    ANTIMATTER(7, "antimatter", 10,
        colors =  intArrayOf(0x0000EB, 0x000000, 0xFFFFFF),
        properties = setOf(Matter(), Dust, Plate(20))
    ),
    PURE_ANTIMATTER_TIER0(8, "pure_antimatter", 11,
        colors =  intArrayOf(0xFF32FF, 0x000000, 0xFFFFFF),
        properties = setOf(Matter(), Dust, Plate(20))
    ),
    // specific case, block -> plate recipe should be added manually
    OCTUPLE_ENERGETIC_CLAY(9, "oec", 12,
        colors =  intArrayOf(0xFFFF00, 0x8C8C8C, 0xFFFFFF),
        properties = setOf(Dust, Plate(20))
    ),
    PURE_ANTIMATTER_TIER8(10, "opa", 13,
        colors =  intArrayOf(0x960000, 0xC8C800, 0xFFFFFF),
        properties = setOf(Matter("matter5"), Dust, Plate(20))
    ),

    /* pure antimatter 1x~7x */
    PURE_ANTIMATTER_TIER1(11, "pure_antimatter_tier1", 11, intArrayOf(0xC42385, 0x191919, 0xFFFFFF), setOf(Matter("matter2"))),
    PURE_ANTIMATTER_TIER2(12, "pure_antimatter_tier2", 11, intArrayOf(0x8E1777, 0x323200, 0xFFFFFF), setOf(Matter("matter2"))),
    PURE_ANTIMATTER_TIER3(13, "pure_antimatter_tier3", 11, intArrayOf(0x5E0D45, 0x4B4B00, 0xFFFFFF), setOf(Matter("matter3"))),
    PURE_ANTIMATTER_TIER4(14, "pure_antimatter_tier4", 12, intArrayOf(0x32061F, 0x646400, 0xFFFFFF), setOf(Matter("matter3"))),
    PURE_ANTIMATTER_TIER5(15, "pure_antimatter_tier5", 12, intArrayOf(0x520829, 0x7D7D00, 0xFFFFFF), setOf(Matter("matter4"))),
    PURE_ANTIMATTER_TIER6(16, "pure_antimatter_tier6", 12, intArrayOf(0x6E0727, 0x969600, 0xFFFFFF), setOf(Matter("matter4"))),
    PURE_ANTIMATTER_TIER7(17, "pure_antimatter_tier7", 12, intArrayOf(0x840519, 0xAFAF00, 0xFFFFFF), setOf(Matter("matter4"))),

    AZ91D(18, "az91d", 6,
        colors = intArrayOf(0x828C87, 0x0A280A, 0xFFFFFF),
        properties = setOf(Ingot, Dust, Plate(20))
    ),
    ZK60A(19, "zk60a", 6,
        colors = intArrayOf(0x4B5550, 0x0A280A, 0xFFFFFF),
        properties = setOf(Ingot, Dust, Plate(20))
    ),

    BARIUM(20, "barium",
        colors = intArrayOf(0x965078, 0x781450, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0x965078, 0x78783C, 0xDCDCDC))
    ),
    BERYLLIUM(21, "beryllium",
        colors = intArrayOf(0xD2F0D2, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xD2F0D2, 0x78783C, 0xDCDCDC))
    ),
    BRASS(22, "brass",
        colors = intArrayOf(0xBEAA14, 0x000000, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    BRONZE(23, "bronze",
        colors = intArrayOf(0xFA9628, 0x000000, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    CALCIUM(24, "calcium",
        colors = intArrayOf(0xF0F0F0, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xF0F0F0, 0x78783C, 0xDCDCDC))
    ),
    CHROME(25, "chrome",
        colors = intArrayOf(0xF0D2D2, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    COPPER(26, "copper",
        colors = intArrayOf(0xA05A0A, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xA05A0A, 0x78783C, 0xDCDCDC))
    ),
    ELECTRUM(27, "electrum",
        colors = intArrayOf(0xE6E69B, 0x787846, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    HAFNIUM(28, "hafnium",
        colors = intArrayOf(0xF0D2AA, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xF0D2AA, 0x78783C, 0xDCDCDC))
    ),
    INVAR(29, "invar",
        colors = intArrayOf(0xAAAA50, 0x8C8C46, 0xB4B450),
        properties = setOf(Ingot, Dust)
    ),
    LEAD(30, "lead",
        colors = intArrayOf(0xBEF0D2, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xBEF0D2, 0x78783C, 0xDCDCDC))
    ),
    LITHIUM(31, "lithium",
        colors = intArrayOf(0xD2D296, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xDCDC96, 0x78783C, 0xDCDCDC))
    ),
    MANGANESE(32, "manganese",
        colors = intArrayOf(0xBEF0F0, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xBEF0F0, 0x78783C, 0xDCDCDC))
    ),
    MAGNESIUM(33, "magnesium",
        colors = intArrayOf(0x96D296, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0x96DC96, 0x78783C, 0xDCDCDC))
    ),
    NICKEL(34, "nickel",
        colors = intArrayOf(0xD2D2F0, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xD2D2F0, 0x78783C, 0xDCDCDC))
    ),
    POTASSIUM(35, "potassium",
        colors = intArrayOf(0xF0F0BE, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xF0F0BE, 0x78783C, 0xDCDCDC))
    ),
    SODIUM(36, "sodium",
        colors = intArrayOf(0xAAAADE, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xAAAAE6, 0x78783C, 0xDCDCDC))
    ),
    STEEL(37, "steel",
        colors = intArrayOf(0x5A5A6E, 0x000000, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    STRONTIUM(38, "strontium",
        colors = intArrayOf(0xD2AAF2, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xD2AAF2, 0x78783C, 0xDCDCDC))
    ),
    TITANIUM(39, "titanium",
        colors = intArrayOf(0xD2F0F0, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xD2F0F0, 0x78783C, 0xDCDCDC))
    ),
    ZINC(40, "zinc",
        colors = intArrayOf(0xE6AAAA, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xE6AAAA, 0x78783C, 0xDCDCDC))
    ),
    ZIRCONIUM(41, "zirconium",
        colors = intArrayOf(0xBEAA7A, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xBEAA7A, 0x78783C, 0xDCDCDC))
    ),

    ACTINIUM(42, "actinium",
        colors = intArrayOf(0xF5F5F5, 0x0000EB, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    AMERICIUM(43, "americium",
            colors = intArrayOf(0xEBEBEB, 0x9B9B9B, 0xEBEBEB),
            properties = setOf(Ingot)
    ),
    ANTIMONY(44, "antimony",
        colors = intArrayOf(0x464646, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    BISMUTH(45, "bismuth",
        colors = intArrayOf(0x467846, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    CAESIUM(46, "caesium",
        colors = intArrayOf(0xF5F5F5, 0x969600, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    CERIUM(47, "cerium",
        colors = intArrayOf(0x919191, 0x969600, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    COBALT(48, "cobalt",
        colors = intArrayOf(0x1E1EE6, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    CURIUM(49, "curium",
        colors = intArrayOf(0xFFFFFF, 0x9B9B9B, 0xF4F4F4),
        properties = setOf(Ingot)
    ),
    EUROPIUM(50, "europium",
        colors = intArrayOf(0x919191, 0x373737, 0x919191),
        properties = setOf(Ingot)
    ),
    FRANCIUM(51, "francium",
        colors = intArrayOf(0xF5F5F5, 0x00EB00, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    IRIDIUM(52, "iridium",
        colors = intArrayOf(0xF0F0F0, 0xD2D2D2, 0xEBEBEB),
        properties = setOf(Ingot)
    ),
    LANTHANUM(53, "lanthanum",
        colors = intArrayOf(0x919191, 0xEB0000, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    MOLYBDENUM(54, "molybdenum",
        colors = intArrayOf(0x82A082, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    NEODYMIUM(55, "neodymium",
        colors = intArrayOf(0x919191, 0x009696, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    NEPTUNIUM(56, "neptunium",
        colors = intArrayOf(0x3232FF, 0x32329B, 0x3232FF),
        properties = setOf(Ingot)
    ),
    OSMIUM(57, "osmium",
        colors = intArrayOf(0x464696, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PALLADIUM(58, "palladium",
        colors = intArrayOf(0x974646, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PLATINUM(59, "platinum",
        colors = intArrayOf(0xF5F5E6, 0x8C8C78, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PLUTONIUM(60, "plutonium",
        colors = intArrayOf(0xFF3232, 0x9B3232, 0xFF3232),
        properties = setOf(Ingot)
    ),
    PRASEODYMIUM(61, "praseodymium",
        colors = intArrayOf(0x919191, 0x00EB00, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PROMETHIUM(62, "promethium",
        colors = intArrayOf(0x919191, 0x0000EB, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PROTACTINIUM(63, "protactinium",
        colors = intArrayOf(0x323232, 0x191919, 0x323264),
        properties = setOf(Ingot)
    ),
    RADIUM(64, "radium",
        colors = intArrayOf(0xF5F5F5, 0x009696, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    RHENIUM(65, "rhenium",
        colors = intArrayOf(0x464696, 0x191919, 0x32325A),
        properties = setOf(Ingot)
    ),
    RUBIDIUM(66, "rubidium",
        colors = intArrayOf(0xF5F5F5, 0xEB0000, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    SAMARIUM(67, "samarium",
        colors = intArrayOf(0x919191, 0x960096, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    SILVER(68, "silver",
        colors = intArrayOf(0xE6E6F5, 0x78788C, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    TANTALUM(69, "tantalum",
        colors = intArrayOf(0xF0D2AA, 0x191919, 0xF0D296),
        properties = setOf(Ingot)
    ),
    THORIUM(70, "thorium",
        colors = intArrayOf(0x323232, 0x191919, 0xC83232),
        properties = setOf(Ingot)
    ),
    TIN(71, "tin",
        colors = intArrayOf(0xE6E6F0, 0x000000, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    TUNGSTEN(72, "tungsten",
        colors = intArrayOf(0x1E1E1E, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    URANIUM(73, "uranium",
        colors = intArrayOf(0x32FF32, 0x329B32, 0x32FF32),
        properties = setOf(Ingot)
    ),
    VANADIUM(74, "vanadium",
        colors = intArrayOf(0x3C7878, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    ZINC_ALUMINUM(75, "zinc_aluminum",
        colors = intArrayOf(0xF0BEDC, 0xA00000, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    ZINC_ZIRCONIUM(76, "zinc_zirconium",
        colors = intArrayOf(0xE6AA8C, 0x780000, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),

    IMPURE_GLOWSTONE(1000, "impure_glowstone", colors = intArrayOf(0x979746, 0x191919, 0xFFFFFF), properties = setOf(Dust)),
    IMPURE_REDSTONE(1001, "impure_redstone", colors = intArrayOf(0x974646, 0x191919, 0xFFFFFF), properties = setOf(Dust)),
    CALCIUM_CHLORIDE(1002, "calcium_chloride", properties = setOf(Dust)),
    CARBON(1003, "carbon", colors = intArrayOf(0x0A0A0A, 0x191919, 0x1E1E1E), properties = setOf(Dust)),
    CHARCOAL(1004, "coal", colors = intArrayOf(0x141414, 0x191919, 0x503232), properties = setOf(Dust)),
    COAL(1005, "coal", colors = intArrayOf(0x141414, 0x191919, 0x323250), properties = setOf(Dust)),
    LAPIS(1006, "lapis", colors = intArrayOf(0x3C64BE, 0x0A2B7A, 0x5A82E2), properties = setOf(Dust)),
    ORGANIC_CLAY(1007, "organic_clay", colors = intArrayOf(0x8890AD, 0x6A2C2B, 0x92A4B7), properties = setOf(Dust)),
    PHOSPHORUS(1008, "phosphorus", colors = intArrayOf(0xB4B419, 0x9B9B00, 0xCDCD32), properties = setOf(Dust)),
    QUARTZ(1009, "quartz", properties = setOf(Dust)),
    SALT(1010, "salt", properties = setOf(Dust)),
    SALTPETER(1011, "saltpeter", colors = intArrayOf(0xDEDCDC, 0xBEC8D2, 0xFFF0E6), properties = setOf(Dust)),
    SODIUM_CARBONATE(1012, "sodium_carbonate", properties = setOf(Dust)),
    SULFUR(1013, "sulfur", colors = intArrayOf(0xE6E600, 0xCDCD00, 0xFFFF00), properties = setOf(Dust)),
    ;

    val properties: Set<MaterialProperty> = run {
        val set = mutableSetOf<MaterialProperty>()
        properties.forEach { propGiven ->
            if (set.any { propGiven::class == it::class }) {
                throw IllegalArgumentException("Material already has property of type ${propGiven::class.simpleName}")
            }
            set.add(propGiven)
        }
        set.toSet()
    }

    inline fun <reified T : MaterialProperty> hasProperty(): Boolean {
        return properties.firstOrNull { it is T } != null
    }

    inline fun <reified T : MaterialProperty> getProperty(): T? {
        return properties.first { it is T } as? T
    }

    companion object {
        private val uniqueIdMap: IntIdentityHashBiMap<Material> = run {
            val map = IntIdentityHashBiMap<Material>(entries.size)
            entries.forEach {
                val exceptNull = map.get(it.uniqueId)
                if (exceptNull != null) {
                    throw IllegalArgumentException("Material id ${it.uniqueId} is already occupied by ${exceptNull.materialName}, cannot register ${it.materialName}")
                }
                map.put(it, it.uniqueId)
            }
            map
        }

        fun getId(material: Material): Int {
            return uniqueIdMap.getId(material)
        }

        fun fromId(id: Int): Material? {
            return uniqueIdMap[id]
        }

    }
}