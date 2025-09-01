package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.capability.IClayiumWorkable

interface IRecipeProcessor : IClayiumWorkable {
    /**
     * var for syncing
     */
    override var currentProgress: Long

    /**
     * var for syncing
     */
    override var requiredProgress: Long

    val isCompleted: Boolean
    val hasRecipe: Boolean
    fun tick()

    fun set(recipe: IClayiumRecipe)
    fun reset()
}