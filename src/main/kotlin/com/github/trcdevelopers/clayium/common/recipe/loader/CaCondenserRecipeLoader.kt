package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial

object CaCondenserRecipeLoader {
    fun registerRecipes() {
        CRecipes.CA_CONDENSER.register {
            input(MetaItemClayParts.ANTIMATTER_SEED)
            output(OrePrefix.MATTER, EnumMaterial.ANTIMATTER)
            cePerTick(ClayEnergy.of(250))
            duration(2000)
            tier(0)
        }
    }
}