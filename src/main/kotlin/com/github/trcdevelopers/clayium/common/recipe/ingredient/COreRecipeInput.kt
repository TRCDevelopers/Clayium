package com.github.trcdevelopers.clayium.common.recipe.ingredient

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

class COreRecipeInput(
    val oreId: Int,
    override val amount: Int,
) : CRecipeInput() {
    override val stacks by lazy {
        OreDictionary.getOres(OreDictionary.getOreName(oreId)).map {
            it.copy().apply { count = amount }
        }
    }

    override fun testItemStackAndAmount(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        return stacks.any {
            OreDictionary.itemMatches(it, stack, false) && stack.count >= amount
        }
    }
}