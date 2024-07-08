package com.github.trcdevelopers.clayium.common.recipe.ingredient

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

class COreRecipeInput(
    val oreId: Int,
    override val amount: Int,
) : CRecipeInput() {

    constructor(oreDict: String, amount: Int = 1) : this(OreDictionary.getOreID(oreDict), amount)

    override val stacks by lazy {
        val oreStacks = OreDictionary.getOres(OreDictionary.getOreName(oreId)).map {
            it.copy().apply { count = amount }
        }
        validate(oreStacks)
        oreStacks
    }

    override fun testItemStackAndAmount(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        return stacks.any {
            OreDictionary.itemMatches(it, stack, false) && stack.count >= amount
        }
    }

    override fun toString() = OreDictionary.getOreName(oreId)
}