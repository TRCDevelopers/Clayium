package com.github.trc.clayium.common.recipe.ingredient

import com.github.trc.clayium.api.unification.stack.ItemAndMeta
import net.minecraft.item.ItemStack

class CItemRecipeInput(
    override val stacks: List<ItemStack>,
    override val amount: Int,
    isConsumable: Boolean = true,
): CRecipeInput(isConsumable) {

    constructor(stack: ItemStack, amount: Int, isConsumable: Boolean): this(listOf(stack), amount, isConsumable)

    override fun testItemStackAndAmount(stack: ItemStack): Boolean {
        return stacks.any {
            ItemStack.areItemsEqual(it, stack)
                    && (!stack.hasSubtypes || stack.metadata == it.metadata || stack.metadata == 32767)
                    && stack.count >= amount
        }
    }

    override fun testIgnoringAmount(item: ItemAndMeta): Boolean {
        return stacks.any {
            item.item == it.item && (item.meta == it.metadata || it.metadata == 32767)
        }
    }

    override fun toString(): String {
        return "CItemRecipeInput($stacks)"
    }
}