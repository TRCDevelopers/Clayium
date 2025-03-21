package com.github.trc.clayium.common.recipe.ingredient

import com.github.trc.clayium.api.unification.stack.ItemAndMeta
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

class CItemRecipeInput(
    override val stacks: List<ItemStack>,
    override val amount: Int,
    isConsumable: Boolean = true,
): CRecipeInput(isConsumable) {

    init {
        require(stacks.all { !it.isEmpty }) { "Empty ItemStack is not allowed." }
    }

    constructor(stack: ItemStack, isConsumable: Boolean = true): this(listOf(stack), stack.count, isConsumable)
    constructor(stack: ItemStack, amount: Int, isConsumable: Boolean = true): this(listOf(stack), amount, isConsumable)

    override fun testItemStackAndAmount(stack: ItemStack): Boolean {
        return stacks.any {
            OreDictionary.itemMatches(it, stack, false)
                    && stack.count >= amount
        }
    }

    override fun testIgnoringAmount(item: ItemAndMeta): Boolean {
        return stacks.any {
            OreDictionary.itemMatches(it, item.asStack(), false)
        }
    }

    override fun toString(): String {
        return "CItemRecipeInput($stacks)"
    }
}