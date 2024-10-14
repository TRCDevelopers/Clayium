package com.github.trc.clayium.common.recipe.ingredient

import com.github.trc.clayium.api.unification.stack.ItemAndMeta
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import com.github.trc.clayium.api.util.copyWithSize
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

class CMultiOreRecipeInput(
    override val amount: Int,
    vararg oreDicts: UnificationEntry,
) : CRecipeInput() {

    val oreIds = oreDicts.map { OreDictionary.getOreID(it.toString()) }

    override val stacks by lazy {
        val oreStacks =
            oreIds
                .map {
                    OreDictionary.getOres(OreDictionary.getOreName(it)).map {
                        it.copyWithSize(amount)
                    }
                }
                .flatten()
        oreStacks
    }

    override fun testItemStackAndAmount(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        return stacks.any { OreDictionary.itemMatches(it, stack, false) && stack.count >= amount }
    }

    override fun testIgnoringAmount(item: ItemAndMeta): Boolean {
        return stacks.any { it.item == item.item && it.metadata == item.meta }
    }

    override fun toString(): String {
        return "CMultiOreRecipeInput(${oreIds.map { OreDictionary.getOreName(it) }.joinToString(", ")})"
    }
}
