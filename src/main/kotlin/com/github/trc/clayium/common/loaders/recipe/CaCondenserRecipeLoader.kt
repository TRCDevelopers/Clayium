package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes

object CaCondenserRecipeLoader {
    fun registerRecipes() {
        CRecipes.CA_CONDENSER.register {
            input(MetaItemClayParts.AntimatterSeed)
            output(OrePrefix.gem, CMaterials.antimatter)
            CEt(ClayEnergy.of(250))
            duration(2000)
            tier(0)
        }
    }
}