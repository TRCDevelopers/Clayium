package io.github.trcdevelopers.clayium.common.recipe.builder

import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix

class CaInjectorRecipeBuilder : RecipeBuilder<CaInjectorRecipeBuilder> {

    constructor() : super()
    constructor(another: CaInjectorRecipeBuilder) : super(another)

    override fun copy(): CaInjectorRecipeBuilder {
        return CaInjectorRecipeBuilder(this)
    }

    fun inputAntimatter(amount: Int): CaInjectorRecipeBuilder {
        return this.input(OrePrefix.gem, CMaterials.antimatter, amount)
    }

    fun inputPureAntimatter(amount: Int): CaInjectorRecipeBuilder {
        return this.input(OrePrefix.gem, CMaterials.pureAntimatter, amount)
    }
}