package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import com.github.trcdevelopers.clayium.common.recipe.RecipeInput
import com.google.common.base.Preconditions
import net.minecraft.item.ItemStack

class ClayWorkTableRecipe(
    val input: RecipeInput,
    primaryOutput: ItemStack,
    secondaryOutput: ItemStack,
    private val method: ClayWorkTableMethod,
    val clicks: Int
) {

    val primaryOutput = primaryOutput
        get() = field.copy()
    val secondaryOutput = secondaryOutput
        get() = field.copy()

    constructor(input: RecipeInput, primaryOutput: ItemStack, method: ClayWorkTableMethod, clicks: Int) : this(input, primaryOutput, ItemStack.EMPTY, method, clicks)

    val outputs get() = listOf(primaryOutput.copy(), secondaryOutput.copy())

    init {
        Preconditions.checkArgument(clicks > 0, "Clicks must be greater than 0, got $clicks")
    }


    fun hasSecondaryOutput(): Boolean {
        return !secondaryOutput.isEmpty
    }

    fun matches(input: ItemStack, method: ClayWorkTableMethod): Boolean {
        return this.method == method && this.input.test(input)
    }

}
