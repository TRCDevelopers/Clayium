package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.google.common.base.Preconditions
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.OreIngredient

class ClayWorkTableRecipe(
    val input: RecipeInput,
    primaryOutput: ItemStack,
    secondaryOutput: ItemStack,
    val method: ClayWorkTableMethod,
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


    class Builder {
        private lateinit var input: RecipeInput
        private lateinit var primaryOutput: ItemStack
        private var secondaryOutput: ItemStack = ItemStack.EMPTY
        private lateinit var method: ClayWorkTableMethod
        private var clicks: Int = 1

        fun input(input: RecipeInput) { this.input = input }
        fun primaryOutput(primaryOutput: ItemStack) { this.primaryOutput = primaryOutput }
        fun secondaryOutput(secondaryOutput: ItemStack) { this.secondaryOutput = secondaryOutput }
        fun method(method: ClayWorkTableMethod) { this.method = method }
        fun clicks(clicks: Int) {
            Preconditions.checkArgument(clicks > 0, "Clicks must be greater than 0, got $clicks")
            this.clicks = clicks
        }

        fun input(item: Item, amount: Int = 1) { input(RecipeInput(item, amount)) }
        fun input(item: MetaItemClayium.MetaValueItem, amount: Int = 1) { input(RecipeInput(Ingredient.fromStacks(item.stackForm), amount)) }
        fun input(prefix: OrePrefix, material: Material, amount: Int = 1) { input(RecipeInput(OreIngredient(prefix.concat(material)), amount)) }

        fun primaryOutput(prefix: OrePrefix, material: Material, amount: Int = 1) {
            val stack = OreDictionary.getOres(prefix.concat(material)).firstOrNull()?.copy() ?: throw IllegalArgumentException("No oredict entry for ${prefix.concat(material)}")
            stack.count = amount
            primaryOutput(stack)
        }
        fun secondaryOutput(prefix: OrePrefix, material: Material, amount: Int = 1) {
            val stack = OreDictionary.getOres(prefix.concat(material)).firstOrNull()?.copy() ?: throw IllegalArgumentException("No oredict entry for ${prefix.concat(material)}")
            stack.count = amount
            secondaryOutput(stack)
        }

        fun build(): ClayWorkTableRecipe {
            return ClayWorkTableRecipe(input, primaryOutput, secondaryOutput, method, clicks)
        }
    }
}
