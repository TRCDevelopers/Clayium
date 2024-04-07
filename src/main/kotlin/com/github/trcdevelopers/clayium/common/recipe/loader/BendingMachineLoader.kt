package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.recipe.CRecipes
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Ingot
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Matter
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty.Plate

object BendingMachineLoader {
    fun register() {
        val registry = CRecipes.getSimpleCeRecipeRegistry(MachineBlocks.Name.BENDING)!!

        for (material in Material.entries) {
            if (material.hasProperty<Ingot>() && material.hasProperty<Plate>()) {
                val plateProp = material.getProperty<Plate>() ?: continue
                registry.register {
                    input(OrePrefix.INGOT, material)
                    outputs(OreDictUnifier.get(OrePrefix.PLATE, material))
                    cePerTick(plateProp.cePerTick)
                    requiredTicks(plateProp.requiredTick)
                    tier(plateProp.tier)
                }
            }

            if (material.hasProperty<Matter>() && material.hasProperty<Plate>()) {
                val plateProp = material.getProperty<Plate>() ?: continue
                registry.register {
                    input(OrePrefix.MATTER, material)
                    outputs(OreDictUnifier.get(OrePrefix.PLATE, material))
                    cePerTick(plateProp.cePerTick)
                    requiredTicks(plateProp.requiredTick)
                    tier(plateProp.tier)
                }
            }

            if (material.hasProperty<Plate>()) {
                registerLargePlateRecipe(material)
            }
        }
    }

    private fun registerLargePlateRecipe(material: Material) {
        val plateProp = material.getProperty<Plate>() ?: return
        CRecipes.getSimpleCeRecipeRegistry(MachineBlocks.Name.BENDING)
            ?.register {
                input(OrePrefix.PLATE, material, 4)
                outputs(OreDictUnifier.get(OrePrefix.LARGE_PLATE, material))
                cePerTick(plateProp.cePerTick)
                requiredTicks(plateProp.requiredTick * 4)
                tier(plateProp.tier)
            }
    }
}