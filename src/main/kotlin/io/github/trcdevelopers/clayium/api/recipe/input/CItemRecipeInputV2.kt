package io.github.trcdevelopers.clayium.api.recipe.input

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

class CItemRecipeInputV2(
    override val components: List<ItemStack>,
    override val requiredAmount: Int,
    override val consumeAmount: Int = requiredAmount,
) : IRecipeInput<ItemStack> {

    override fun test(input: ItemStack): Boolean {
        return components.any {
            OreDictionary.itemMatches(it, input, false) && input.count >= requiredAmount
        }
    }

    override fun testIgnoringAmount(input: ItemStack): Boolean {
        return components.any {
            OreDictionary.itemMatches(it, input, false)
        }
    }

    override fun equals(other: Any?): Boolean {
        TODO()
    }

    override fun hashCode(): Int {
        TODO()
    }
}