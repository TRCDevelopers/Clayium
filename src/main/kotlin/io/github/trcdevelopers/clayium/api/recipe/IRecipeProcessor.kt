package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.capability.IClayiumWorkable

interface IRecipeProcessor : IClayiumWorkable {
    val isCompleted: Boolean
    val hasRecipe: Boolean
    fun tick()

    /**
     * Given `requiredProgress` is raw. Not affected by overclocking or any other factors.
     */
    fun set(requiredProgress: Int)
}