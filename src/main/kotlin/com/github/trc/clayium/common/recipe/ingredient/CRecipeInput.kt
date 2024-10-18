package com.github.trc.clayium.common.recipe.ingredient

import com.github.trc.clayium.api.unification.stack.ItemAndMeta
import com.github.trc.clayium.api.util.CLog
import net.minecraft.item.ItemStack

abstract class CRecipeInput(val isConsumable: Boolean = true) {

    abstract val stacks: List<ItemStack>
    abstract val amount: Int
    // $amount is abstract so it must be lazy
    val consumeAmount: Int by lazy { if (isConsumable) amount else 0 }

    abstract fun testItemStackAndAmount(stack: ItemStack): Boolean

    abstract fun testIgnoringAmount(item: ItemAndMeta): Boolean

    fun isValid(): Boolean {
        if (stacks.isEmpty()) {
            CLog.error("Stacks must not be empty")
            return false
        }
        if (!stacks.all { it.count == amount }) {
            CLog.error("All stacks must have the same amount")
            return false
        }
        return true
    }
}
