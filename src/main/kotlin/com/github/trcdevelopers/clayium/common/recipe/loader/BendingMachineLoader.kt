package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
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
                    requiredTicks(plateProp.requiredTick)
                    //todo
                    cePerTick(ClayEnergy.of(100))
                }
            }

            if (material.hasProperty<Matter>() && material.hasProperty<Plate>()) {
                val plateProp = material.getProperty<Plate>() ?: continue
                registry.register {
                    input(OrePrefix.MATTER, material)
                    outputs(OreDictUnifier.get(OrePrefix.PLATE, material))
                    requiredTicks(plateProp.requiredTick)
                    //todo
                    cePerTick(ClayEnergy.of(100))
                }
            }
        }
    }
}