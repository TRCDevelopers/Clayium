package io.github.trcdevelopers.clayium.common.recipe.ingredient

import io.github.trcdevelopers.clayium.api.unification.stack.ItemAndMeta
import io.github.trcdevelopers.clayium.api.util.CLog
import net.minecraft.item.ItemStack

abstract class CRecipeInput(val isConsumable: Boolean = true) {

    abstract val stacks: List<ItemStack>
    abstract val amount: Int
    // $amount is abstract so it must be lazy
    val consumeAmount: Int by lazy { if (isConsumable) amount else 0 }

    abstract fun testItemStackAndAmount(stack: ItemStack): Boolean
    abstract fun testIgnoringAmount(item: ItemAndMeta): Boolean

    private val hash: Int by lazy {
        // If overflow occurs, it will be negative, but it is not a problem.
        var result = isConsumable.hashCode()
        result = 31 * result + amount
        result = 31 * result + consumeAmount
        for (s in stacks) {
            result = 31 * result + s.item.hashCode()
            result = 31 * result + s.metadata
            result = 31 * result + s.count
            result = 31 * result + s.tagCompound.hashCode()
        }

        result
    }

    fun isValid(): Boolean {
        if (stacks.isEmpty()) {
            CLog.error("Stacks must not be empty")
            return false
        }
        if (!stacks.all { it.count == amount }) {
            CLog.error("All stacks must have the same amount")
            return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CRecipeInput) return false

        if (isConsumable != other.isConsumable) return false
        if (amount != other.amount) return false
        if (consumeAmount != other.consumeAmount) return false
        if (stacks.size != other.stacks.size) return false
        for ((s, o) in stacks.zip(other.stacks)) {
            if (!ItemStack.areItemStacksEqual(s, o)) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        return this.hash
    }
}