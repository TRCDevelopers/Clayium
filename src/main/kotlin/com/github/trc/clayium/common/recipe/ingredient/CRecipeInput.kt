package com.github.trc.clayium.common.recipe.ingredient

import com.github.trc.clayium.api.unification.stack.ItemAndMeta
import com.github.trc.clayium.common.Clayium
import net.minecraft.item.ItemStack

abstract class CRecipeInput {

    abstract val stacks: List<ItemStack>
    abstract val amount: Int

    abstract fun testItemStackAndAmount(stack: ItemStack): Boolean
    abstract fun testIgnoringAmount(item: ItemAndMeta): Boolean

    fun isValid(): Boolean {
        if (stacks.isEmpty()) {
            Clayium.LOGGER.error("Stacks must not be empty")
            return false
        }
        if (!stacks.all { it.count == amount }) {
            Clayium.LOGGER.error("All stacks must have the same amount")
            return false
        }
        return true
    }
}