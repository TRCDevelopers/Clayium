package io.github.trcdevelopers.clayium.common.util

import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.common.items.ClayiumItems
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

object FluidStackUtils {

    private val capsuleCapacities = intArrayOf(1, 5, 25, 125, 1000)

    private val capsuleItems = listOf(
        ClayiumItems.FLUID_CAPSULE_1000MB,
        ClayiumItems.FLUID_CAPSULE_125MB,
        ClayiumItems.FLUID_CAPSULE_25MB,
        ClayiumItems.FLUID_CAPSULE_5MB,
        ClayiumItems.FLUID_CAPSULE_1MB,
    )

    // TODO fluidStack.amountが多すぎると処理にとてつもない時間がかかってしまう
    fun toCapsules(fluidStack: FluidStack): List<ItemStack> {
        val fluid = fluidStack.fluid
        var remainder = fluidStack.amount
        val capsules = mutableListOf<ItemStack>()
        for (i in capsuleCapacities.indices) {
            val capacity = capsuleCapacities[i]
            if (remainder <= 0) break
            val thisCapacityCount = remainder / capacity
            if (thisCapacityCount > 0) {
                val stack = capsuleItems[i].setFluid(fluid)
                val stacksCount = thisCapacityCount / stack.maxStackSize
                val stackCountRemainder = thisCapacityCount % stack.maxStackSize
                repeat(stacksCount) {
                    capsules.add(stack.copyWithSize(stack.maxStackSize))
                }
                if (stackCountRemainder > 0) {
                    capsules.add(stack.copyWithSize(stackCountRemainder))
                }
                remainder -= thisCapacityCount * capacity
            }
        }
        return capsules
    }
}