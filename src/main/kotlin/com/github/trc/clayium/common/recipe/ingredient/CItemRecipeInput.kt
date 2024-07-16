package com.github.trc.clayium.common.recipe.ingredient

import net.minecraft.item.ItemStack

class CItemRecipeInput(
    override val stacks: List<ItemStack>,
    override val amount: Int,
): CRecipeInput() {

    init {
        validate(stacks)
    }

    override fun testItemStackAndAmount(stack: ItemStack): Boolean {
        return stacks.any {
            ItemStack.areItemsEqual(it, stack)
                    && (!stack.hasSubtypes || stack.metadata == it.metadata || stack.metadata == 32767)
                    && stack.count >= amount
        }
    }
}