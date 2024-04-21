package com.github.trcdevelopers.clayium.common.recipe.builder

class SimpleRecipeBuilder() : RecipeBuilder<SimpleRecipeBuilder>() {


    override fun copy(): SimpleRecipeBuilder {
        return SimpleRecipeBuilder()
            .inputs(*inputs.toTypedArray())
            .outputs(*outputs.toTypedArray())
            .duration(duration)
            .cePerTick(cePerTick)
            .tier(tier)
    }
}