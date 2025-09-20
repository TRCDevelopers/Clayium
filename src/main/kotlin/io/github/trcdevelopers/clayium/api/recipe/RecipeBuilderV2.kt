package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.FALLBACK
import io.github.trcdevelopers.clayium.api.FALLBACK_L
import io.github.trcdevelopers.clayium.api.recipe.input.CItemRecipeInputV2

@Suppress("UNCHECKED_CAST")
abstract class RecipeBuilderV2<T: RecipeBuilderV2<T>>(
) {

    private var itemInputs: List<CItemRecipeInputV2> = emptyList()
    private var outputs: IRecipeOutputs? = null
    private var duration: Long = FALLBACK_L
    private var cePerTick: ClayEnergy = ClayEnergy.ZERO
    private var recipeTier: Int = FALLBACK
    private var priority: Int = 0

    fun build(): RecipeV2 {
        val outputs = this.outputs
            ?: throw IllegalStateException("Outputs must be set to build a recipe")
        if (duration < 0) {
            throw IllegalStateException("Duration must be >= 0 to build a recipe")
        }
        if (recipeTier < 0) {
            throw IllegalStateException("Recipe tier must be >= 0 to build a recipe")
        }

        return RecipeV2(
            this.itemInputs, outputs,
            this.duration, this.cePerTick,
            this.recipeTier, this.priority
            )
    }
}