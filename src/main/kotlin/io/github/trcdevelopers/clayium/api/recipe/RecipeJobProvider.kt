package io.github.trcdevelopers.clayium.api.recipe

interface RecipeJobProvider {
    fun provide(): RecipeProcessingJob?
}