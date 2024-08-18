package com.github.trc.clayium.common.recipe

import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import com.github.trc.clayium.common.blocks.clayworktable.ClayWorkTableMethod
import com.github.trc.clayium.common.items.metaitem.MetaItemClayium
import com.google.common.base.Preconditions
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
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
        private val outputs: MutableList<ItemStack> = mutableListOf()
        private lateinit var method: ClayWorkTableMethod
        private var clicks: Int = 1

        fun input(input: RecipeInput) { this.input = input }
        fun method(method: ClayWorkTableMethod) { this.method = method }
        fun clicks(clicks: Int) {
            Preconditions.checkArgument(clicks > 0, "Clicks must be greater than 0, got $clicks")
            this.clicks = clicks
        }

        fun input(item: Item, amount: Int = 1) { input(RecipeInput(item, amount)) }
        fun input(item: MetaItemClayium.MetaValueItem, amount: Int = 1) { input(RecipeInput(Ingredient.fromStacks(item.getStackForm(1)), amount)) }
        fun input(orePrefix: OrePrefix, material: CMaterial, amount: Int = 1) { input(RecipeInput(OreIngredient(UnificationEntry(orePrefix, material).toString()), amount)) }

        fun output(itemStack: ItemStack) { outputs.add(itemStack) }
        fun output(item: Item, amount: Int = 1) { output(ItemStack(item, amount)) }
        fun output(item: MetaItemClayium.MetaValueItem, amount: Int = 1) { output(item.getStackForm(amount)) }
        fun output(orePrefix: OrePrefix, material: CMaterial, amount: Int = 1) { output(OreDictUnifier.get(orePrefix, material, 1)) }

        fun build(): ClayWorkTableRecipe {
            val primaryOutput = outputs[0]
            val secondaryOutput = if (outputs.size > 1) outputs[1] else ItemStack.EMPTY
            return ClayWorkTableRecipe(input, primaryOutput, secondaryOutput, method, clicks)
        }
    }
}
