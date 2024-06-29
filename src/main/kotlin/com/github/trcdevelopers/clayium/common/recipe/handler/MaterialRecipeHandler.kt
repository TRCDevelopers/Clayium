package com.github.trcdevelopers.clayium.common.recipe.handler

import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.util.clayiumId
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.oredict.ShapedOreRecipe

object MaterialRecipeHandler {
    fun registerRecipes() {
        for (material in ClayiumApi.materialRegistry) {
            if (material.hasOre(OrePrefix.ingot)) {
                if (material.hasProperty(PropertyKey.PLATE)) addPlateRecipe(OrePrefix.ingot, material)
                tryAddGrindingRecipe(OrePrefix.ingot, material)
            }

            if (material.hasOre(OrePrefix.matter)) {
                if (material.hasProperty(PropertyKey.PLATE)) addPlateRecipe(OrePrefix.matter, material)
                tryAddGrindingRecipe(OrePrefix.matter, material)
            }

            if (material.hasOre(OrePrefix.dust)) {
                handleDust(OrePrefix.dust, material)
            }

            if (material.hasOre(OrePrefix.impureDust)) {

            }

            if (material.hasOre(OrePrefix.block)) {
                if (material.hasProperty(PropertyKey.PLATE)) addPlateRecipe(OrePrefix.block, material)
                if (material.hasProperty(PropertyKey.CLAY)) {
                    val prop = material.getProperty(PropertyKey.CLAY)
                    if (prop.compressedInto != null) addClayBlockRecipe(material, prop.compressedInto)
                }
                tryAddGrindingRecipe(OrePrefix.block, material)
            }

            if (material.hasOre(OrePrefix.plate)) { tryAddGrindingRecipe(OrePrefix.plate, material) }
            if (material.hasOre(OrePrefix.largePlate)) { tryAddGrindingRecipe(OrePrefix.largePlate, material, 4) }
        }
    }

    private fun Material.hasOre(orePrefix: OrePrefix): Boolean {
        return !OreDictUnifier.get(orePrefix, this).isEmpty
    }

    private fun handleDust(dustPrefix: OrePrefix, material: Material) {
        val tier = material.tier?.numeric ?: 0
        fun addDustCondenseRecipe(outputPrefix: OrePrefix) {
            if (OreDictUnifier.get(outputPrefix, material).isEmpty) return
            CRecipes.CONDENSER.register {
                input(dustPrefix, material)
                output(outputPrefix, material)
                cePerTick(if (tier < 10) ClayEnergy.micro(10) else ClayEnergy.micro(250))
                duration(if (tier < 10) 5 else 80)
                tier(if (tier < 10) 0 else 10)
            }
        }

        addDustCondenseRecipe(OrePrefix.block)
        addDustCondenseRecipe(OrePrefix.matter)
    }

    private fun handleImpureDust(material: Material) {
        val impureDust = OrePrefix.impureDust
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

    private fun tryAddGrindingRecipe(inputPrefix: OrePrefix, material: Material, outputAmount: Int = 1) {
        if (OreDictUnifier.get(OrePrefix.dust, material).isEmpty) return
        CRecipes.GRINDER.register {
            input(inputPrefix, material)
            output(OrePrefix.dust, material, outputAmount)
            cePerTick(ClayEnergy.micro(250))
            duration(4)
            tier(0)
        }
    }

    private fun addClayBlockRecipe(material: Material, compressedInto: Material) {
        val clayProperty = material.getPropOrNull(PropertyKey.CLAY)
        val resultClayProperty = compressedInto.getProperty(PropertyKey.CLAY)
        // generate recipe for non-energy clay blocks
        if (clayProperty != null && resultClayProperty.energy == null) {
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