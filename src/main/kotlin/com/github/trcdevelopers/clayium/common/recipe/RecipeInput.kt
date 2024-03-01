package com.github.trcdevelopers.clayium.common.recipe

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import java.util.function.Predicate

class RecipeInput(
    private val stacks: Ingredient,
    val amount: Int,
) : Predicate<ItemStack> {

    constructor(item: Item, amount: Int) : this(Ingredient.fromStacks(ItemStack(item, 1)), amount)
    constructor(itemStack: ItemStack) : this(Ingredient.fromStacks(itemStack), itemStack.count)

    // [Ingredient.matchingStacks] returns an empty list in [OreIngredient], so use [Ingredient.getMatchingStacks] instead.
    val inputStacks = stacks.getMatchingStacks().map { it.copy().apply { this@apply.count = this@RecipeInput.amount } }

    override fun test(input: ItemStack): Boolean {
        return stacks.test(input) && input.count >= amount
    }
}