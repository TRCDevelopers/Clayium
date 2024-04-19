package com.github.trcdevelopers.clayium.common.recipe.ingredient

import com.google.common.collect.ImmutableList
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack

abstract class CRecipeInput {

    abstract val stacks: List<ItemStack>
    abstract val amount: Int

    init {
        require(stacks.isNotEmpty()) { "Stacks must not be empty" }
        require(stacks.all { it.count == amount }) { "All stacks must have the same amount" }
    }

    abstract fun testItemStackAndAmount(stack: ItemStack): Boolean
}