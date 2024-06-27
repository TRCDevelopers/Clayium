package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.EnumOrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

object CondenserRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CONDENSER

        registry.register {
            input(OrePrefix.dust, CMaterials.clay)
            output(ItemStack(Blocks.CLAY))
            cePerTick(ClayEnergy.micro(10))
            duration(3)
            tier(0)
        }
    }
}