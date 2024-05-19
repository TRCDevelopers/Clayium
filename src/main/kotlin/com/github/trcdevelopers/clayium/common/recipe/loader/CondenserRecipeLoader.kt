package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

object CondenserRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CONDENSER

        registry.register {
            input(OrePrefix.DUST, Material.CLAY)
            output(ItemStack(Blocks.CLAY))
            cePerTick(ClayEnergy.micro(10))
            duration(3)
            tier(0)
        }
    }
}