package com.github.trc.clayium.common.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.common.recipe.chanced.ChancedOutputList
import com.github.trc.clayium.common.recipe.ingredient.CRecipeInput
import com.google.common.collect.ImmutableList
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

data class Recipe(
    val inputs: List<CRecipeInput>,
    val outputs: List<ItemStack>,
    val chancedOutputs: ChancedOutputList<ItemStack>?,
    val duration: Long,
    val cePerTick: ClayEnergy,
    /**
     * if `machine.tier.numeric < recipe.tier`, then the recipe is not matched
     */
    val tierNumeric: Int,
) {
    fun matches(consumeOnMatch: Boolean, inputsIn: IItemHandlerModifiable, tier: Int): Boolean {

        if (this.tierNumeric > tier) return false
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
            for (i in 0..<inputsIn.size) {
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

    fun copyOutputs(): List<ItemStack> {
        val resultOutputs = mutableListOf<ItemStack>()
        resultOutputs.addAll(outputs.map { it.copy() })
        if (chancedOutputs != null) {
            resultOutputs.addAll(chancedOutputs.roll().map { it.copy() })
        }
        return ImmutableList.copyOf(resultOutputs)
    }

    override fun toString(): String {
        return "Recipe(inputs=$inputs, outputs=$outputs, duration=$duration, cePerTick=$cePerTick, tierNumeric=$tierNumeric)"
    }
}