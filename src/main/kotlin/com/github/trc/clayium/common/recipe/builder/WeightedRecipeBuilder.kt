package com.github.trc.clayium.common.recipe.builder

import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.chanced.ChancedOutputList
import com.github.trc.clayium.common.recipe.chanced.IChancedOutputLogic.Companion.MAX_CHANCE

class WeightedRecipeBuilder : RecipeBuilder<WeightedRecipeBuilder> {
    constructor() : super()

    constructor(another: WeightedRecipeBuilder) : super(another)

    override fun copy() = WeightedRecipeBuilder(this)

    override fun buildAndRegister() {
        setDefaults()
        val totalWeight = chancedOutputs.sumOf { it.chance }
        val chanceUnit = MAX_CHANCE / totalWeight
        val finalizedOutputs = chancedOutputs.map { it.copy(chance = chanceUnit * it.chance) }
        val chancedOutputList = ChancedOutputList(finalizedOutputs, ChancedOutputList.WEIGHTED)
        val recipe = Recipe(inputs, outputs, chancedOutputList, duration, cePerTick, tier)
        recipeRegistry.addRecipe(recipe)
    }
}
