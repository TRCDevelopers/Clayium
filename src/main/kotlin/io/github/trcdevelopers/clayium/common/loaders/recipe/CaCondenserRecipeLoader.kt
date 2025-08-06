package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes

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