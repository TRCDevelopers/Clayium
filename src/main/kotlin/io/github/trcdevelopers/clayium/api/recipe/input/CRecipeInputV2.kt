package io.github.trcdevelopers.clayium.api.recipe.input

import net.minecraft.item.ItemStack

@Suppress("EqualsOrHashCode") // equals is abstract, so inheritors must implement.
abstract class CRecipeInputV2(
    val requiredAmount: Int,
    val consumeAmount: Int = requiredAmount,
    val isConsumable: Boolean = consumeAmount > 0,
) {

    abstract val components: List<ItemStack>
    private var hash = 0
    private var hashCached = false

    open fun test(stack: ItemStack): Boolean {
        return testIgnoringAmount(stack) && stack.count >= requiredAmount
    }
    abstract fun testIgnoringAmount(stack: ItemStack): Boolean

    abstract fun computeHash(): Int

    abstract override fun equals(other: Any?): Boolean
    override fun hashCode(): Int {
        if (!hashCached) {
            hash = computeHash()
            hashCached = true
        }
        return hash
    }
}