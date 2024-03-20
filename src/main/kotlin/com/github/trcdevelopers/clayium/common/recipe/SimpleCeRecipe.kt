package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreIngredient

class SimpleCeRecipe(
    val inputSize: Int,
    val outputSize: Int,
    val inputs: List<RecipeInput>,
    private val outputs: List<ItemStack>,
    val requiredTicks: Int,
    val cePerTick: ClayEnergy,
    val tier: Int,
) {
    /**
     * @return [ItemStack.EMPTY] if index is out of bounds, otherwise the copied output itemStack at given index.
     */
    fun getOutput(index: Int): ItemStack {
        if (index < 0 || index >= outputSize) {
            return ItemStack.EMPTY
        }
        return outputs[index].copy()
    }

    fun matches(vararg inputsIn: ItemStack): Boolean {
        if (inputsIn.size < inputSize) return false
        val unvalidatedInputs = inputs.toMutableList()

        for (givenInput in inputsIn) {
            val iterator = unvalidatedInputs.iterator()
            while (iterator.hasNext()) {
                val recipeInput = iterator.next()
                if (recipeInput.test(givenInput)) {
                    iterator.remove()
                    break
                }
            }
        }

        return unvalidatedInputs.isEmpty()
    }

    class Builder(
        private val inputSize: Int,
        private val outputSize: Int,
    ) {
        private val inputs: MutableList<RecipeInput> = mutableListOf()
        private val outputs: MutableList<ItemStack> = mutableListOf()

        private var requiredTicks: Int = 0
        private var cePerTick: ClayEnergy = ClayEnergy(0)
        private var tier: Int = 0

        fun input(index: Int, input: Item, amount: Int = 1) {
            inputs.add(index, RecipeInput(input, amount))
        }
        fun input(index: Int, input: MetaItemClayium.MetaValueItem, amount: Int = 1) {
            inputs.add(index, RecipeInput(input.getStackForm(amount)))
        }
        fun input(index: Int, input: ItemStack) {
            inputs.add(index, RecipeInput(input))
        }
        fun input(prefix: OrePrefix, material: Material, amount: Int = 1) {
            inputs.add(RecipeInput(OreIngredient(prefix.concat(material)), amount))
        }

        fun outputs(vararg outputs: ItemStack) {
            this.outputs.addAll(outputs)
        }

        fun requiredTicks(requiredTicks: Int) {
            this.requiredTicks = requiredTicks
        }
        fun cePerTick(cePerTick: ClayEnergy) {
            this.cePerTick = cePerTick
        }

        fun tier(tier: Int) {
            this.tier = tier
        }

        fun build(): SimpleCeRecipe {
            return SimpleCeRecipe(inputSize, outputSize, inputs, outputs, requiredTicks, cePerTick, tier)
        }
    }
}