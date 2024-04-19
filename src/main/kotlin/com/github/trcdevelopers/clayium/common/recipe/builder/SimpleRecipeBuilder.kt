package com.github.trcdevelopers.clayium.common.recipe.builder

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.ingredient.CRecipeInput
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.recipe.registry.SimpleCeRecipeRegistry
import net.minecraft.item.ItemStack

class SimpleRecipeBuilder(
    registry: RecipeRegistry,
    inputs: MutableList<CRecipeInput>,
    outputs: MutableList<ItemStack>,
    duration: Int,
    cePerTick: ClayEnergy,
    tier: Int,
) : RecipeBuilder<SimpleRecipeBuilder>(registry, inputs, outputs, duration, cePerTick, tier) {

    constructor(another: SimpleRecipeBuilder) : this(another.recipeRegistry, another.inputs, another.outputs, another.duration, another.cePerTick, another.tier)

    override fun copy(): SimpleRecipeBuilder {
        return SimpleRecipeBuilder(this)
    }
}