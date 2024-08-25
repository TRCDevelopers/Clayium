package com.github.trc.clayium.common.recipe.ingredient

import com.github.trc.clayium.api.unification.stack.ItemAndMeta
import net.minecraft.item.ItemStack

abstract class CRecipeInput {

    abstract val stacks: List<ItemStack>
    abstract val amount: Int

    abstract fun testItemStackAndAmount(stack: ItemStack): Boolean
    abstract fun testIgnoringAmount(item: ItemAndMeta): Boolean

    fun validate(stacks: List<ItemStack>) {
        require(stacks.isNotEmpty()) { "Stacks must not be empty" }
        require(stacks.all { it.count == amount }) { "All stacks must have the same amount" }
    }
}