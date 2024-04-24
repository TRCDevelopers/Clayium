package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.ingredient.CRecipeInput
import com.github.trcdevelopers.clayium.common.util.CUtils
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

class Recipe(
    val inputs: List<CRecipeInput>,
    val outputs: List<ItemStack>,
    val duration: Int,
    val cePerTick: ClayEnergy,
    val tier: Int,
) {
    fun matches(consumeOnMatch: Boolean, inputsIn: IItemHandlerModifiable, tierIn: Int): Boolean {

        if (this.tier > tierIn) return false
        val (isItemsMatched, amountsToConsume) = matchesItems(CUtils.handlerToList(inputsIn))
        if (!isItemsMatched) return false

        if (consumeOnMatch) {
            for (i in amountsToConsume.indices) {
                inputsIn.extractItem(i, amountsToConsume[i], false)
            }
        }
        return true
    }

    fun matches(inputsIn: List<ItemStack>): Boolean {
        return matchesItems(inputsIn).first
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

    override fun toString(): String {
        return "Recipe(inputs=$inputs, outputs=$outputs, tier=$tier, duration=$duration, cePerTick=$cePerTick)"
    }
}