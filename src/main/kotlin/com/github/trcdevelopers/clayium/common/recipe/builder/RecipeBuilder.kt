package com.github.trcdevelopers.clayium.common.recipe.builder

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.recipe.ingredient.CItemRecipeInput
import com.github.trcdevelopers.clayium.common.recipe.ingredient.COreRecipeInput
import com.github.trcdevelopers.clayium.common.recipe.ingredient.CRecipeInput
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.stack.UnificationEntry
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

@Suppress("UNCHECKED_CAST")
abstract class RecipeBuilder<R: RecipeBuilder<R>>(
    protected val recipeRegistry: RecipeRegistry<R>,
    protected val inputs: MutableList<CRecipeInput>,
    protected val outputs: MutableList<ItemStack>,
    protected var duration: Int,
    protected var cePerTick: ClayEnergy,
    protected var tier: Int,
) {
    constructor(recipeRegistry: RecipeRegistry<R>) : this(recipeRegistry, mutableListOf(), mutableListOf(), 0, ClayEnergy.ZERO, 0)

    abstract fun copy(): R

    fun input(input: CRecipeInput): R {
        if (input.amount <= 0) {
            Clayium.LOGGER.error("input amount must be greater than 0")
            return this as R
        }
        inputs.add(input)
        return this as R
    }

    fun input(stack: ItemStack) = input(CItemRecipeInput(listOf(stack), stack.count))
    fun input(item: Item, amount: Int = 1) = input(ItemStack(item, amount))
    fun input(metaItem: MetaItemClayium.MetaValueItem, amount: Int = 1) = input(metaItem.getStackForm(amount))
    fun input(block: Block, amount: Int = 1) = input(ItemStack(block, amount))
    fun input(oreDict: String, amount: Int = 1) = input(COreRecipeInput(oreDict, amount))
    fun input(orePrefix: OrePrefix, material: Material, amount: Int = 1) = input(COreRecipeInput(UnificationEntry(orePrefix, material).toString(), amount))

    fun outputs(vararg stacks: ItemStack): R {
        outputs.addAll(stacks)
        return this as R
    }

    fun output(stack: ItemStack) = outputs(stack)
    fun output(item: Item, amount: Int = 1) = output(ItemStack(item, amount))
    fun output(metaItem: MetaItemClayium.MetaValueItem, amount: Int = 1) = output(metaItem.getStackForm(amount))
    fun output(block: Block, amount: Int = 1) = output(ItemStack(block, amount))
    fun output(oreDict: String, amount: Int = 1) = outputs(OreDictUnifier.get(oreDict, amount))
    fun output(orePrefix: OrePrefix, material: Material, amount: Int = 1) = outputs(OreDictUnifier.get(orePrefix, material, amount))

    fun duration(duration: Int): R {
        this.duration = duration
        return this as R
    }

    fun cePerTick(cePerTick: ClayEnergy): R {
        this.cePerTick = cePerTick
        return this as R
    }

    fun tier(tier: Int): R {
        this.tier = tier
        return this as R
    }

    fun buildAndRegister() {
        recipeRegistry.addRecipe(
            Recipe(inputs, outputs, duration, cePerTick, tier)
        )
    }
}