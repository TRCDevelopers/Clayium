package com.github.trcdevelopers.clayium.common.recipe.ingredient

import net.minecraft.item.ItemStack

abstract class CRecipeInput {

    abstract val stacks: List<ItemStack>
    abstract val amount: Int

    abstract fun testItemStackAndAmount(stack: ItemStack): Boolean

    fun validateThis() {
        require(stacks.isNotEmpty()) { "Stacks must not be empty" }
        require(stacks.all { it.count == amount }) { "All stacks must have the same amount" }
    }
}