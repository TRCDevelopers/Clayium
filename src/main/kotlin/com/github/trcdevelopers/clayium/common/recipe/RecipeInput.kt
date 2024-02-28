package com.github.trcdevelopers.clayium.common.recipe

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import java.util.function.Predicate

class RecipeInput(
    private val stacks: Ingredient,
    val amount: Int,
) : Predicate<ItemStack> {

    val inputStacks = stacks.matchingStacks.toList()

    override fun test(input: ItemStack): Boolean {
        return stacks.test(input) && input.count >= amount
    }
}