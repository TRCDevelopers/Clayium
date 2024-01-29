package com.github.trcdevelopers.clayium.common.items.metaitem

import com.github.trcdevelopers.clayium.common.items.ColoredMaterial
import com.github.trcdevelopers.clayium.common.items.IMaterial
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.model.ModelLoader

@Suppress("unused")
object MetaItemIngot : MetaItemClayium("ingot") {

    private val INGOT_TEXTURE = ModelResourceLocation("clayium:colored/ingot", "inventory")

    val SILICONE = fromMaterial(0, ColoredMaterial.SILICONE)
    val SILICON = fromMaterial(1, ColoredMaterial.SILICON)
    val ALUMINUM = fromMaterial(2, ColoredMaterial.ALUMINUM)
    val CLAY_STEEL = fromMaterial(3, ColoredMaterial.CLAY_STEEL)
    val CLAYIUM = fromMaterial(4, ColoredMaterial.CLAYIUM)
    val ULTIMATE_ALLOY = fromMaterial(5, ColoredMaterial.ULTIMATE_ALLOY)
    val ANTIMATTER = fromMaterial(6, ColoredMaterial.ANTIMATTER)
    val PURE_ANTIMATTER = fromMaterial(7, ColoredMaterial.PURE_ANTIMATTER_TIER0)
    val OCE = fromMaterial(8, ColoredMaterial.OCTUPLE_ENERGETIC_CLAY)
    val OPA = fromMaterial(9, ColoredMaterial.PURE_ANTIMATTER_TIER8)

    val AZ100D = fromMaterial(10, ColoredMaterial.AZ91D)
    val ZK69A = fromMaterial(11, ColoredMaterial.ZK60A)

    val BARIUM = fromMaterial(12, ColoredMaterial.BARIUM)
    val BERYLLIUM = fromMaterial(13, ColoredMaterial.BERYLLIUM)
    val BRASS = fromMaterial(14, ColoredMaterial.BRASS)
    val BRONZE = fromMaterial(15, ColoredMaterial.BRONZE)
    val CALCIUM = fromMaterial(16, ColoredMaterial.CALCIUM)
    val CHROME = fromMaterial(17, ColoredMaterial.CHROME)
    val COPPER = fromMaterial(18, ColoredMaterial.COPPER)
    val ELECTRUM = fromMaterial(19, ColoredMaterial.ELECTRUM)
    val HAFNIUM = fromMaterial(20, ColoredMaterial.HAFNIUM)
    val INVAR = fromMaterial(21, ColoredMaterial.INVAR)
    val LEAD = fromMaterial(22, ColoredMaterial.LEAD)
    val LITHIUM = fromMaterial(23, ColoredMaterial.LITHIUM)
    val MANGANESE = fromMaterial(24, ColoredMaterial.MANGANESE)
    val MAGNESIUM = fromMaterial(25, ColoredMaterial.MAGNESIUM)
    val NICKEL = fromMaterial(26, ColoredMaterial.NICKEL)
    val POTASSIUM = fromMaterial(27, ColoredMaterial.POTASSIUM)
    val SODIUM = fromMaterial(28, ColoredMaterial.SODIUM)
    val STEEL = fromMaterial(29, ColoredMaterial.STEEL)
    val STRONTIUM = fromMaterial(30, ColoredMaterial.STRONTIUM)
    val TITANIUM = fromMaterial(31, ColoredMaterial.TITANIUM)
    val ZINC = fromMaterial(32, ColoredMaterial.ZINC)
    val ZIRCONIUM = fromMaterial(33, ColoredMaterial.ZIRCONIUM)

    val ACTINIUM = fromMaterial(34, ColoredMaterial.ACTINIUM)
    val AMERICIUM = fromMaterial(35, ColoredMaterial.AMERICIUM)
    val ANTIMONY = fromMaterial(36, ColoredMaterial.ANTIMONY)
    val BISMUTH = fromMaterial(37, ColoredMaterial.BISMUTH)
    val CAESIUM = fromMaterial(38, ColoredMaterial.CAESIUM)
    val CERIUM = fromMaterial(39, ColoredMaterial.CERIUM)
    val COBALT = fromMaterial(40, ColoredMaterial.COBALT)
    val CURIUM = fromMaterial(41, ColoredMaterial.CURIUM)
    val EUROPIUM = fromMaterial(42, ColoredMaterial.EUROPIUM)
    val FRANCIUM = fromMaterial(43, ColoredMaterial.FRANCIUM)
    val IRIDIUM = fromMaterial(44, ColoredMaterial.IRIDIUM)
    val LANTHANUM = fromMaterial(45, ColoredMaterial.LANTHANUM)
    val MOLYBDENUM = fromMaterial(46, ColoredMaterial.MOLYBDENUM)
    val NEODYMIUM = fromMaterial(47, ColoredMaterial.NEODYMIUM)
    val NEPTUNIUM = fromMaterial(48, ColoredMaterial.NEPTUNIUM)
    val OSMIUM = fromMaterial(49, ColoredMaterial.OSMIUM)
    val PALLADIUM = fromMaterial(50, ColoredMaterial.PALLADIUM)
    val PLATINUM = fromMaterial(51, ColoredMaterial.PLATINUM)
    val PLUTONIUM = fromMaterial(50, ColoredMaterial.PLUTONIUM)
    val PRASEODYMIUM = fromMaterial(51, ColoredMaterial.PRASEODYMIUM)
    val PROMETHIUM = fromMaterial(52, ColoredMaterial.PROMETHIUM)
    val PROTACTINIUM = fromMaterial(53, ColoredMaterial.PROTACTINIUM)
    val RADIUM = fromMaterial(54, ColoredMaterial.RADIUM)
    val RHENIUM = fromMaterial(55, ColoredMaterial.RHENIUM)
    val RUBIDIUM = fromMaterial(56, ColoredMaterial.RUBIDIUM)
    val SAMARIUM = fromMaterial(57, ColoredMaterial.SAMARIUM)
    val SILVER = fromMaterial(58, ColoredMaterial.SILVER)
    val TANTALUM = fromMaterial(59, ColoredMaterial.TANTALUM)
    val THORIUM = fromMaterial(60, ColoredMaterial.THORIUM)
    val TIN = fromMaterial(61, ColoredMaterial.TIN)
    val TUNGSTEN = fromMaterial(62, ColoredMaterial.TUNGSTEN)
    val URANIUM = fromMaterial(63, ColoredMaterial.URANIUM)
    val VANADIUM = fromMaterial(64, ColoredMaterial.VANADIUM)
    val ZINC_ALUMINUM = fromMaterial(65, ColoredMaterial.ZINC_ALUMINUM)
    val ZINC_ZIRCONIUM = fromMaterial(66, ColoredMaterial.ZINC_ZIRCONIUM)

    override fun registerModels() {
        for (item in metaValueItems.values) {
            ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), INGOT_TEXTURE)
        }
    }

    private fun fromMaterial(meta: Short, material: IMaterial): MetaValueItem {
        return addItem(meta, "${material.materialName}_ingot")
            .tier(material.tier)
            .oreDict("ingot${material.oreDictSuffix}")
    }
}