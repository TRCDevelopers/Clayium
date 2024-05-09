package com.github.trcdevelopers.clayium.common.recipe.registry

import com.github.trcdevelopers.clayium.common.recipe.builder.RecipeBuilder
import com.github.trcdevelopers.clayium.common.recipe.builder.SimpleRecipeBuilder

object CRecipes {
    private val REGISTRY = mutableMapOf<String, RecipeRegistry<*>>()
    val ALL_REGISTRIES: Map<String, RecipeRegistry<*>> get() = REGISTRY.toMap()
    val BENDING = addRegistry("bending_machine", SimpleRecipeBuilder(), 1, 1)

    fun <R: RecipeBuilder<R>> addRegistry(name: String, buildSample: R, inputSize: Int, outputSize: Int): RecipeRegistry<R> {
        val registry = RecipeRegistry(name, buildSample, inputSize, outputSize)
        REGISTRY[name] = registry
        return registry
    }

    fun findRegistry(name: String): RecipeRegistry<*>? {
        return REGISTRY[name]
    }
}