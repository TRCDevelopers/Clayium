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

    private val DUST_TEXTURE = ModelResourceLocation("clayium:colored/dust", "inventory")

    val CLAY = addItem(0, "clay_dust").tier(1).oreDict("dustClay")
    val DENSE_CLAY = addItem(1, "dense_clay_dust").tier(2)
    val INDUSTRIAL_CLAY = addItem(2, "industrial_clay_dust").tier(3)
    val ADV_INDUSTRIAL_CLAY = addItem(3, "adv_industrial_clay_dust").tier(4)
    val CALCIUM_CHLORIDE = addItem(4, "calcium_chloride_dust").tier(4)
    val SODIUM_CARBONATE = addItem(5, "sodium_carbonate_dust").tier(4)
    val QUARTZ = addItem(6, "quartz_dust").tier(4)
    val EXCITED_CLAY = addItem(7, "excited_clay_dust").tier(7)
    val SALT = addItem(8, "salt_dust").tier(4).oreDict("dustSalt")

    val SILICONE = fromMaterial(9, ColoredMaterial.SILICONE)
    val SILICON = fromMaterial(10, ColoredMaterial.SILICON)
    val ALUMINUM = fromMaterial(11, ColoredMaterial.ALUMINUM)
    val CLAY_STEEL = fromMaterial(12, ColoredMaterial.CLAY_STEEL)
    val CLAYIUM = fromMaterial(13, ColoredMaterial.CLAYIUM)
    val ULTIMATE_ALLOY = fromMaterial(14, ColoredMaterial.ULTIMATE_ALLOY)
    val ANTIMATTER = fromMaterial(15, ColoredMaterial.ANTIMATTER)
    val PURE_ANTIMATTER = fromMaterial(16, ColoredMaterial.PURE_ANTIMATTER_TIER0)
    val OCE = fromMaterial(17, ColoredMaterial.OCTUPLE_ENERGETIC_CLAY)
    val OPA = fromMaterial(18, ColoredMaterial.PURE_ANTIMATTER_TIER8)

    val AZ100D = fromMaterial(10, ColoredMaterial.AZ91D)
    val ZK69A = fromMaterial(11, ColoredMaterial.ZK60A)

    val BARIUM = fromMaterial(21, ColoredMaterial.BARIUM)
    val BERYLLIUM = fromMaterial(22, ColoredMaterial.BERYLLIUM)
    val BRASS = fromMaterial(23, ColoredMaterial.BRASS)
    val BRONZE = fromMaterial(24, ColoredMaterial.BRONZE)
    val CALCIUM = fromMaterial(25, ColoredMaterial.CALCIUM)
    val CHROME = fromMaterial(26, ColoredMaterial.CHROME)
    val COPPER = fromMaterial(27, ColoredMaterial.COPPER)
    val ELECTRUM = fromMaterial(28, ColoredMaterial.ELECTRUM)
    val HAFNIUM = fromMaterial(29, ColoredMaterial.HAFNIUM)
    val INVAR = fromMaterial(30, ColoredMaterial.INVAR)
    val LEAD = fromMaterial(31, ColoredMaterial.LEAD)
    val LITHIUM = fromMaterial(32, ColoredMaterial.LITHIUM)
    val MANGANESE = fromMaterial(33, ColoredMaterial.MANGANESE)
    val MAGNESIUM = fromMaterial(34, ColoredMaterial.MAGNESIUM)
    val NICKEL = fromMaterial(35, ColoredMaterial.NICKEL)
    val POTASSIUM = fromMaterial(36, ColoredMaterial.POTASSIUM)
    val SODIUM = fromMaterial(37, ColoredMaterial.SODIUM)
    val STEEL = fromMaterial(38, ColoredMaterial.STEEL)
    val STRONTIUM = fromMaterial(39, ColoredMaterial.STRONTIUM)
    val TITANIUM = fromMaterial(40, ColoredMaterial.TITANIUM)
    val ZINC = fromMaterial(41, ColoredMaterial.ZINC)
    val ZIRCONIUM = fromMaterial(42, ColoredMaterial.ZIRCONIUM)

    @SideOnly(Side.CLIENT)
    override fun registerModels() {
        for (item in this.metaValueItems.values) {
            if (item is MetaValueItemDust) {
                ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), DUST_TEXTURE)
            } else {
                registerModel(item)
            }
        }
    }

    private fun fromMaterial(meta: Short, material: IMaterial): MetaValueItem {
        val item = MetaValueItemDust(meta, "${material.materialName}_dust")
            .tier(material.tier)
            .oreDict("dust${material.oreDictSuffix}")
            .addComponent(IItemColorHandler { _, i -> material.colors[i] })
        metaValueItems[meta] = item
        return item
    }

    private class MetaValueItemDust(meta: Short, name: String,) : MetaValueItem(meta, name)
}