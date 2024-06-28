package com.github.trcdevelopers.clayium.common.recipe.handler

import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.util.clayiumId
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.ShapedOreRecipe

object MaterialRecipeHandler {
    fun registerRecipes() {
        for (material in ClayiumApi.materialRegistry) {
            if (material.hasOre(OrePrefix.ingot)) handleIngot(material)
            if (material.hasOre(OrePrefix.block)) handleBlock(material)
        }
    }

    private fun Material.hasOre(orePrefix: OrePrefix): Boolean {
        return !OreDictUnifier.get(orePrefix, this).isEmpty
    }

    private fun handleIngot(material: Material) {
        if (material.hasProperty(PropertyKey.PLATE)) addPlateRecipe(OrePrefix.ingot, material)
    }

    private fun handleDust(material: Material) {

    }

    private fun handleBlock(material: Material) {
        if (material.hasProperty(PropertyKey.PLATE)) addPlateRecipe(OrePrefix.block, material)
        if (material.hasProperty(PropertyKey.CLAY)) {
            val prop = material.getProperty(PropertyKey.CLAY)
            if (prop.compressedInto != null) addClayBlockRecipe(material, prop.compressedInto)
        }
    }

    /**
     * Adds plate and largePlate recipe for [material].
     * Assumes that [material] has a plate property.
     */
    private fun addPlateRecipe(inputPrefix: OrePrefix, material: Material) {
        val plateProperty = material.getProperty(PropertyKey.PLATE)
        CRecipes.BENDING.register {
            input(inputPrefix, material)
            output(OrePrefix.plate, material)
            cePerTick(plateProperty.cePerTick)
            duration(plateProperty.requiredTick)
            tier(plateProperty.tier)
        }

        CRecipes.BENDING.register {
            input(OrePrefix.plate, material, 4)
            output(OrePrefix.largePlate, material)
            cePerTick(plateProperty.cePerTick * 2)
            duration(plateProperty.requiredTick * 2)
            tier(plateProperty.tier)
        }
    }

    private fun addClayBlockRecipe(material: Material, compressedInto: Material) {
        val clayProperty = material.getPropOrNull(PropertyKey.CLAY)
        if (clayProperty != null) {
            ForgeRegistries.RECIPES.register(
                ShapedOreRecipe(clayiumId("${compressedInto.materialId.path}_block"),
                    OreDictUnifier.get(OrePrefix.block, compressedInto),
                    "CCC",
                    "CCC",
                    "CCC",
                    'C', OreDictUnifier.get(OrePrefix.block, material)
            ).setRegistryName(clayiumId("${compressedInto.materialId.path}_block_1")))
        }
    }
}