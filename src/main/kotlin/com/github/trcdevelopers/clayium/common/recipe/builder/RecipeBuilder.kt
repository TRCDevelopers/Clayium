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
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.stack.UnificationEntry
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

@Suppress("UNCHECKED_CAST")
abstract class RecipeBuilder<R: RecipeBuilder<R>>(
    protected val inputs: MutableList<CRecipeInput>,
    protected val outputs: MutableList<ItemStack>,
    protected var duration: Long,
    protected var cePerTick: ClayEnergy,
    protected var tier: Int,
) {
    constructor() : this(mutableListOf(), mutableListOf(), 0, ClayEnergy.ZERO, 0)

    constructor(another: RecipeBuilder<R>) : this(another.inputs.toMutableList(), another.outputs.toMutableList(), another.duration, another.cePerTick, another.tier) {
        recipeRegistry = another.recipeRegistry
    }

    protected lateinit var recipeRegistry: RecipeRegistry<R>

    abstract fun copy(): R

    fun setRegistry(registry: RecipeRegistry<R>): R {
        recipeRegistry = registry
        return this as R
    }

    fun inputs(vararg inputsIn: CRecipeInput): R {
        inputsIn.forEach { input ->
            if (input.amount <= 0) {
                Clayium.LOGGER.error("input amount must be greater than 0")
                return@forEach
            }
            inputs.add(input)
        }
        return this as R
    }

    fun input(stack: ItemStack) = inputs(CItemRecipeInput(listOf(stack), stack.count))
    fun input(item: Item, amount: Int = 1) = input(ItemStack(item, amount))
    fun input(metaItem: MetaItemClayium.MetaValueItem, amount: Int = 1) = input(metaItem.getStackForm(amount))
    fun input(block: Block, amount: Int = 1) = input(ItemStack(block, amount))
    fun input(oreDict: String, amount: Int = 1) = inputs(COreRecipeInput(oreDict, amount))
    fun input(orePrefix: OrePrefix, material: Material, amount: Int = 1) = inputs(COreRecipeInput(UnificationEntry(orePrefix, material).toString(), amount))

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

    fun duration(duration: Int) = duration(duration.toLong())
    fun duration(duration: Long): R {
        this.duration = duration
        return this as R
    }

    fun cePerTick(cePerTick: ClayEnergy): R {
        this.cePerTick = cePerTick
        return this as R
    }

    /**
     * | Tier | CEt |
     * |:----:|:---:|
     * | 0   | 10u   |
     * | 1 | 10u |
     * | 2 | 10u |
     * | 3 | 100u |
     * | 4 | 1m |
     * | 5 | 10m |
     * | 6 | 100m |
     * | 7 | 1 |
     * | 8 | 10 |
     * | 9 | 100 |
     * | 10 | 1k |
     * | 11 | 10k |
     * | 12 | 100k |
     * | 13 | 1M |
     */
    @Suppress("FunctionName")
    fun CEt(tier: Int): R {
        return this.CEt(1.0, tier)
    }

    @Suppress("FunctionName")
    fun CEt(factor: Double): R {
        return this.CEt(factor, this.tier)
    }

    @Suppress("FunctionName")
    fun CEt(factor: Double, tier: Int): R{
        return this.cePerTick(ClayEnergy((factor * 100.0 * Math.pow(10.0, tier - 4.0)).toLong().coerceAtLeast(1)))
    }

    fun tier(tier: Int): R {
        this.tier = tier
        return this as R
    }

    open fun buildAndRegister() {
        recipeRegistry.addRecipe(
            Recipe(inputs, outputs, duration, cePerTick, tier)
        )
    }
}