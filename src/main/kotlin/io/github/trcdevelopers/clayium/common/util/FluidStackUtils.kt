package io.github.trcdevelopers.clayium.common.util

import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.common.items.ClayiumItems
import io.github.trcdevelopers.clayium.common.items.ItemFluidCapsule
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import kotlin.math.min

object FluidStackUtils {

    private val capsuleItems = listOf(
        ClayiumItems.FLUID_CAPSULE_1000MB,
        ClayiumItems.FLUID_CAPSULE_125MB,
        ClayiumItems.FLUID_CAPSULE_25MB,
        ClayiumItems.FLUID_CAPSULE_5MB,
        ClayiumItems.FLUID_CAPSULE_1MB,
    )
    private val capsuleCapacities = capsuleItems.map(ItemFluidCapsule::capacity).toIntArray()

    val capsuleToAmount: Object2IntOpenHashMap<ItemFluidCapsule> = capsuleItems
        .associateWithTo(Object2IntOpenHashMap()) {
            it.capacity
        }

    fun toCapsules(fluidStack: FluidStack, maxFluidAmount: Int): List<ItemStack> {
        val fluid = fluidStack.fluid
        var remainder = min(fluidStack.amount, maxFluidAmount)
        val capsules = mutableListOf<ItemStack>()
        for (i in capsuleCapacities.indices) {
            if (remainder <= 0) break
            val capacity = capsuleCapacities[i]
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