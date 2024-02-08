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
    val colors: IntArray = intArrayOf(0xFFFFFF),
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
        properties = setOf(Matter, Dust, Plate(20))
    ),
    PURE_ANTIMATTER_TIER0(8, "pure_antimatter", 11,
        colors =  intArrayOf(0xFF32FF, 0x000000, 0xFFFFFF),
        properties = setOf(Matter, Dust, ImpureDust(0xFF32FF, 0x000000, 0xFFFFFF), Plate(20))
    ),
    // specific case, block -> plate recipe should be added manually
    OCTUPLE_ENERGETIC_CLAY(9, "oec", 12,
        colors =  intArrayOf(0xFFFF00, 0x8C8C8C, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xFFFF00, 0x8C8C8C, 0xFFFFFF))
    ),
    PURE_ANTIMATTER_TIER8(10, "opa", 13,
        colors =  intArrayOf(0x960000, 0xC8C800, 0xFFFFFF),
        properties = setOf(Matter, Dust, ImpureDust(0x960000, 0xC8C800, 0xFFFFFF), Plate(20))
    ),

    AZ91D(11, "az91d", 6,
        colors = intArrayOf(0x828C87, 0x0A280A, 0xFFFFFF),
        properties = setOf(Ingot, Dust, Plate(20))
    ),
    ZK60A(12, "zk60a", 6,
        colors = intArrayOf(0x4B5550, 0x0A280A, 0xFFFFFF),
        properties = setOf(Ingot, Dust, Plate(20))
    ),

    // region Ingot & Dust
    BARIUM(13, "barium",
        colors = intArrayOf(0x965078, 0x781450, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0x965078, 0x78783C, 0xDCDCDC))
    ),
    BERYLLIUM(14, "beryllium",
        colors = intArrayOf(0xD2F0D2, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xD2F0D2, 0x78783C, 0xDCDCDC))
    ),
    BRASS(15, "brass",
        colors = intArrayOf(0xBEAA14, 0x000000, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    BRONZE(16, "bronze",
        colors = intArrayOf(0xFA9628, 0x000000, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    CALCIUM(17, "calcium",
        colors = intArrayOf(0xF0F0F0, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xF0F0F0, 0x78783C, 0xDCDCDC))
    ),
    CHROME(18, "chrome",
        colors = intArrayOf(0xF0D2D2, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    COPPER(19, "copper",
        colors = intArrayOf(0xA05A0A, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xA05A0A, 0x78783C, 0xDCDCDC))
    ),
    ELECTRUM(20, "electrum",
        colors = intArrayOf(0xE6E69B, 0x787846, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    HAFNIUM(21, "hafnium",
        colors = intArrayOf(0xF0D2AA, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xF0D2AA, 0x78783C, 0xDCDCDC))
    ),
    INVAR(22, "invar",
        colors = intArrayOf(0xAAAA50, 0x8C8C46, 0xB4B450),
        properties = setOf(Ingot, Dust)
    ),
    LEAD(23, "lead",
        colors = intArrayOf(0xBEF0D2, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xBEF0D2, 0x78783C, 0xDCDCDC))
    ),
    LITHIUM(24, "lithium",
        colors = intArrayOf(0xD2D296, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xDCDC96, 0x78783C, 0xDCDCDC))
    ),
    MANGANESE(25, "manganese",
        colors = intArrayOf(0xBEF0F0, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xBEF0F0, 0x78783C, 0xDCDCDC))
    ),
    MAGNESIUM(26, "magnesium",
        colors = intArrayOf(0x96D296, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0x96DC96, 0x78783C, 0xDCDCDC))
    ),
    NICKEL(27, "nickel",
        colors = intArrayOf(0xD2D2F0, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xD2D2F0, 0x78783C, 0xDCDCDC))
    ),
    POTASSIUM(28, "potassium",
        colors = intArrayOf(0xF0F0BE, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xF0F0BE, 0x78783C, 0xDCDCDC))
    ),
    SODIUM(29, "sodium",
        colors = intArrayOf(0xAAAADE, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xAAAAE6, 0x78783C, 0xDCDCDC))
    ),
    STEEL(30, "steel",
        colors = intArrayOf(0x5A5A6E, 0x000000, 0xFFFFFF),
        properties = setOf(Ingot, Dust)
    ),
    STRONTIUM(31, "strontium",
        colors = intArrayOf(0xD2AAF2, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xD2AAF2, 0x78783C, 0xDCDCDC))
    ),
    TITANIUM(32, "titanium",
        colors = intArrayOf(0xD2F0F0, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xD2F0F0, 0x78783C, 0xDCDCDC))
    ),
    ZINC(33, "zinc",
        colors = intArrayOf(0xE6AAAA, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xE6AAAA, 0x78783C, 0xDCDCDC))
    ),
    ZIRCONIUM(34, "zirconium",
        colors = intArrayOf(0xBEAA7A, 0x787878, 0xFFFFFF),
        properties = setOf(Ingot, Dust, ImpureDust(0xBEAA7A, 0x78783C, 0xDCDCDC))
    ),
    //endregion

    //region Only Ingot
    ACTINIUM(35, "actinium",
        colors = intArrayOf(0xF5F5F5, 0x0000EB, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    AMERICIUM(36, "americium",
            colors = intArrayOf(0xEBEBEB, 0x9B9B9B, 0xEBEBEB),
            properties = setOf(Ingot)
    ),
    ANTIMONY(37, "antimony",
        colors = intArrayOf(0x464646, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    BISMUTH(38, "bismuth",
        colors = intArrayOf(0x467846, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    CAESIUM(39, "caesium",
        colors = intArrayOf(0xF5F5F5, 0x969600, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    CERIUM(40, "cerium",
        colors = intArrayOf(0x919191, 0x969600, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    COBALT(41, "cobalt",
        colors = intArrayOf(0x1E1EE6, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    CURIUM(42, "curium",
        colors = intArrayOf(0xFFFFFF, 0x9B9B9B, 0xF4F4F4),
        properties = setOf(Ingot)
    ),
    EUROPIUM(43, "europium",
        colors = intArrayOf(0x919191, 0x373737, 0x919191),
        properties = setOf(Ingot)
    ),
    FRANCIUM(44, "francium",
        colors = intArrayOf(0xF5F5F5, 0x00EB00, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    IRIDIUM(45, "iridium",
        colors = intArrayOf(0xF0F0F0, 0xD2D2D2, 0xEBEBEB),
        properties = setOf(Ingot)
    ),
    LANTHANUM(46, "lanthanum",
        colors = intArrayOf(0x919191, 0xEB0000, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    MOLYBDENUM(47, "molybdenum",
        colors = intArrayOf(0x82A082, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    NEODYMIUM(48, "neodymium",
        colors = intArrayOf(0x919191, 0x009696, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    NEPTUNIUM(49, "neptunium",
        colors = intArrayOf(0x3232FF, 0x32329B, 0x3232FF),
        properties = setOf(Ingot)
    ),
    OSMIUM(50, "osmium",
        colors = intArrayOf(0x464696, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PALLADIUM(51, "palladium",
        colors = intArrayOf(0x974646, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PLATINUM(52, "platinum",
        colors = intArrayOf(0xF5F5E6, 0x8C8C78, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PLUTONIUM(53, "plutonium",
        colors = intArrayOf(0xFF3232, 0x9B3232, 0xFF3232),
        properties = setOf(Ingot)
    ),
    PRASEODYMIUM(54, "praseodymium",
        colors = intArrayOf(0x919191, 0x00EB00, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PROMETHIUM(55, "promethium",
        colors = intArrayOf(0x919191, 0x0000EB, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    PROTACTINIUM(56, "protactinium",
        colors = intArrayOf(0x323232, 0x191919, 0x323264),
        properties = setOf(Ingot)
    ),
    RADIUM(57, "radium",
        colors = intArrayOf(0xF5F5F5, 0x009696, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    RHENIUM(58, "rhenium",
        colors = intArrayOf(0x464696, 0x191919, 0x32325A),
        properties = setOf(Ingot)
    ),
    RUBIDIUM(59, "rubidium",
        colors = intArrayOf(0xF5F5F5, 0xEB0000, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    SAMARIUM(60, "samarium",
        colors = intArrayOf(0x919191, 0x960096, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    SILVER(61, "silver",
        colors = intArrayOf(0xE6E6F5, 0x78788C, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    TANTALUM(62, "tantalum",
        colors = intArrayOf(0xF0D2AA, 0x191919, 0xF0D296),
        properties = setOf(Ingot)
    ),
    THORIUM(63, "thorium",
        colors = intArrayOf(0x323232, 0x191919, 0xC83232),
        properties = setOf(Ingot)
    ),
    TIN(64, "tin",
        colors = intArrayOf(0xE6E6F0, 0x000000, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    TUNGSTEN(65, "tungsten",
        colors = intArrayOf(0x1E1E1E, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    URANIUM(66, "uranium",
        colors = intArrayOf(0x32FF32, 0x329B32, 0x32FF32),
        properties = setOf(Ingot)
    ),
    VANADIUM(67, "vanadium",
        colors = intArrayOf(0x3C7878, 0x191919, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    ZINC_ALUMINUM(68, "zinc_aluminum",
        colors = intArrayOf(0xF0BEDC, 0xA00000, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    ZINC_ZIRCONIUM(69, "zinc_zirconium",
        colors = intArrayOf(0xE6AA8C, 0x780000, 0xFFFFFF),
        properties = setOf(Ingot)
    ),
    //endregion

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
            entries.forEach { map.put(it, it.uniqueId) }
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