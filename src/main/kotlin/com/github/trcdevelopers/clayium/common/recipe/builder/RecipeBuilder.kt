package com.github.trcdevelopers.clayium.common.recipe.builder

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.ingredient.CRecipeInput
import net.minecraft.item.ItemStack

class RecipeBuilder<R: RecipeBuilder<R>>(
    private val inputs: List<CRecipeInput> = mutableListOf(),
    private val outputs: List<ItemStack> = mutableListOf(),
    private var duration: Int = 0,
    private var cePerTick: ClayEnergy = ClayEnergy(0),
    private var tier: Int = 0,
) {
    private constructor(another: RecipeBuilder<R>) : this(another.inputs, another.outputs, another.duration, another.cePerTick, another.tier)

    @Suppress("UNCHECKED_CAST")
    fun copy(): R {
        return RecipeBuilder(this) as R
    }
}