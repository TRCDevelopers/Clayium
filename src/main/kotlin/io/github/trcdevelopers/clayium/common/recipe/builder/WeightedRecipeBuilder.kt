package io.github.trcdevelopers.clayium.common.recipe.builder

import io.github.trcdevelopers.clayium.common.recipe.Recipe
import io.github.trcdevelopers.clayium.common.recipe.chanced.ChancedOutputList
import io.github.trcdevelopers.clayium.common.recipe.chanced.IChancedOutputLogic.Companion.MAX_CHANCE

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
        val recipe = Recipe(inputs, outputs, chancedOutputList, duration, cePerTick, tier, priority)
        recipeRegistry.addRecipe(recipe)
    }
}