package io.github.trcdevelopers.clayium.api.recipe.input

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

@Suppress("EqualsOrHashCode")
class CItemRecipeInputV2(
    override val components: List<ItemStack>,
    requiredAmount: Int,
    isConsumable: Boolean = true,
) : CRecipeInputV2(requiredAmount, isConsumable) {

    override fun test(stack: ItemStack): Boolean {
        return components.any {
            OreDictionary.itemMatches(it, stack, false) && stack.count >= requiredAmount
        }
    }

    override fun testIgnoringAmount(stack: ItemStack): Boolean {
        return components.any {
            OreDictionary.itemMatches(it, stack, false)
        }
    }

    override fun computeHash(): Int {
        var hash = 1
        for (stack in this.components) {
            hash = 31 * hash + stack.item.hashCode()
            hash = 31 * hash + stack.metadata
            if (stack.hasTagCompound()) {
                hash = 31 * hash + stack.tagCompound.hashCode()
            }
        }
        hash = 31 * hash + requiredAmount
        hash = 31 * hash + if (isConsumable) 1 else 0
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CItemRecipeInputV2) return false

        if (this.requiredAmount != other.requiredAmount) return false
        if (this.isConsumable != other.isConsumable) return false
        if (this.components.size != other.components.size) return false
        for (i in this.components.indices) {
            val s1 = this.components[i]
            val s2 = other.components[i]
            if (!ItemStack.areItemStackTagsEqual(s1, s2)) {
                return false
            }
        }
        return true
    }
}