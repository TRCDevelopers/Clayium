package com.github.trc.clayium.common.recipe.builder

class SimpleRecipeBuilder : RecipeBuilder<SimpleRecipeBuilder> {
    constructor() : super()
    constructor(another: SimpleRecipeBuilder) : super(another)
    override fun copy() = SimpleRecipeBuilder(this)
}