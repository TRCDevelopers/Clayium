package com.github.trcdevelopers.clayium.common.items.metaitem

import com.github.trcdevelopers.clayium.common.items.ColoredMaterial
import com.github.trcdevelopers.clayium.common.items.IMaterial
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemColorHandler
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("unused")
object MetaItemDust : MetaItemClayium("dust") {

    const val DUST_TEXTURE = "clayium:colored/dust"

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

    val AZ91D = fromMaterial(10, ColoredMaterial.AZ91D)
    val ZK60A = fromMaterial(11, ColoredMaterial.ZK60A)

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

    @SideOnly(Side.CLIENT)
    override fun registerModels() {
        for (meta in metaValueItems.keys) {
            ModelLoader.setCustomModelResourceLocation(this, meta.toInt(), ModelResourceLocation("clayium:colored/dust", "inventory"))
        }
    }

    private fun fromMaterial(meta: Short, material: IMaterial): MetaValueItem {
        return addItem(meta, "${material.materialName}_dust")
            .tier(material.tier)
            .oreDict("dust${material.oreDictSuffix}")
            .addComponent(IItemColorHandler { _, i -> material.colors[i] })
    }
}