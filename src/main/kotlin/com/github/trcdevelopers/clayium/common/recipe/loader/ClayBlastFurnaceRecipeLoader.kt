package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.init.Items

object ClayBlastFurnaceRecipeLoader {
    fun register() {
        val registry = CRecipes.CLAY_BLAST_FURNACE

        registry.register {
            input(Items.IRON_INGOT)
            input(Items.COAL, 2)
            output(OrePrefix.INGOT, Material.STEEL)
            cePerTick(ClayEnergy.milli(100))
            duration(500)
            tier(6)
        }
    }
}