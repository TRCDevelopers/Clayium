package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Ingot
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Matter
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Plate

object BendingMachineLoader {
    fun register() {
        val registry = CRecipes.BENDING

        for (material in Material.entries) {
            if (material.hasProperty<Ingot>() && material.hasProperty<Plate>()) {
                val plateProp = material.getProperty<Plate>() ?: continue
                registry.register {
                    input(OrePrefix.INGOT, material)
                    outputs(OreDictUnifier.get(OrePrefix.PLATE, material))
                    cePerTick(plateProp.cePerTick)
                    duration(plateProp.requiredTick)
                    tier(plateProp.tier)
                }
            }

            if (material.hasProperty<Matter>() && material.hasProperty<Plate>()) {
                val plateProp = material.getProperty<Plate>() ?: continue
                registry.register {
                    input(OrePrefix.MATTER, material)
                    outputs(OreDictUnifier.get(OrePrefix.PLATE, material))
                    cePerTick(plateProp.cePerTick)
                    duration(plateProp.requiredTick)
                    tier(plateProp.tier)
                }
            }

            if (material.hasProperty<Plate>()) {
                registerLargePlateRecipe(material)
            }
        }
    }

    private fun registerLargePlateRecipe(material: Material) {
        //todo
        // recipe duration is not always normalPlate * 4
    }
}