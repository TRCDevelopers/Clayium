package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix

object CaCondenserRecipeLoader {
    fun registerRecipes() {
        CRecipes.CA_CONDENSER.register {
            input(MetaItemClayParts.ANTIMATTER_SEED)
            output(OrePrefix.gem, CMaterials.antimatter)
            CEt(ClayEnergy.of(250))
            duration(2000)
            tier(0)
        }
    }
}