package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.ingredient.CRecipeInput
import com.github.trcdevelopers.clayium.common.util.CUtils.listView
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

class Recipe(
    private val inputs: List<CRecipeInput>,
    private val outputs: List<ItemStack>,
    val tier: Int,
    val duration: Int,
    val cePerTick: ClayEnergy,
) {
    fun matches(consumeOnMatch: Boolean, inputsIn: IItemHandlerModifiable): Boolean {

        val (isItemsMatched, amountsToConsume) = matchesItems(inputsIn.listView())
        if (!isItemsMatched) return false

        if (consumeOnMatch) {
            for (i in amountsToConsume.indices) {
                inputsIn.extractItem(i, amountsToConsume[i], false)
            }
        }
        return true
    }

    private fun matchesItems(inputsIn: List<ItemStack>): Pair<Boolean, IntArray> {
        val amountsToConsume = IntArray(inputsIn.size)
        var indexed = 0

        for (ingredient in this.inputs) {
            var isMatched = false
            for (i in 0..inputsIn.size) {
                val itemStack = inputsIn[i]
                if (i == indexed) {
                    amountsToConsume[i] = 0
                    indexed++
                }
                isMatched = ingredient.testItemStackAndAmount(itemStack)
                if (!isMatched) continue
                amountsToConsume[i] = ingredient.amount
                break
            }
            // one of the ingredients is not matched
            if (!isMatched) {
                return Pair(false, intArrayOf())
            }
        }
        // all ingredients are matched
        return Pair(true, IntArray(indexed) { amountsToConsume[it] })
    }
}