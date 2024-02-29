package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.oredict.OreIngredient
import java.util.function.Predicate

class RecipeInput(
    private val stacks: Ingredient,
    val amount: Int,
) : Predicate<ItemStack> {

    constructor(orePrefix: OrePrefix, material: Material, amount: Int) : this(
        OreIngredient(orePrefix.concat(material)),
        amount
    )

    constructor(item: Item, amount: Int) : this(Ingredient.fromStacks(ItemStack(item, 1)), amount)
    constructor(itemStack: ItemStack) : this(Ingredient.fromStacks(itemStack), itemStack.count)

    val inputStacks = stacks.matchingStacks.toList()

    override fun test(input: ItemStack): Boolean {
        return stacks.test(input) && input.count >= amount
    }
}