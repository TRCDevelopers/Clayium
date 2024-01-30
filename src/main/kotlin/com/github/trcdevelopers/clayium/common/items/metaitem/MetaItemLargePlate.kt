package com.github.trcdevelopers.clayium.common.items.metaitem

import com.github.trcdevelopers.clayium.common.items.ColoredMaterial
import com.github.trcdevelopers.clayium.common.items.IMaterial
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemColorHandler
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.model.ModelLoader

@Suppress("unused")
object MetaItemLargePlate : MetaItemClayium("large_plate") {

    private val LARGE_PLATE_TEXTURE = ModelResourceLocation("clayium:colored/large_plate", "inventory")

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

    override fun registerModels() {
        for (item in this.metaValueItems.values) {
            ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), LARGE_PLATE_TEXTURE)
        }
    }

    private fun fromMaterial(meta: Short, material: IMaterial): MetaValueItem {
        return addItem(meta, "large_${material.materialName}_plate")
            .tier(material.tier)
            .oreDict("largePlate${material.oreDictSuffix}")
            .addComponent(IItemColorHandler { _, i -> material.colors[i] })
    }

}