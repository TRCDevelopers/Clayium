package com.github.trcdevelopers.clayium.common.recipe.ingredient

import com.google.common.collect.ImmutableList
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack

class CRecipeInput(
    private val stacks: List<ItemStack>,
    val isConsumable: Boolean = true,
) {
    val amount = stacks[0].count

    val inputStacks = ImmutableList.copyOf(stacks)

    init {
        require(stacks.isNotEmpty()) { "Stacks must not be empty" }
        require(stacks.all { it.count == amount }) { "All stacks must have the same amount" }
    }

    fun testItemStackAndAmount(stack: ItemStack): Boolean {
        return stacks.any {
            ItemStack.areItemsEqual(it, stack)
                    && stack.count >= amount
                    && ItemStack.areItemStackTagsEqual(it, stack)
        }
    }
}