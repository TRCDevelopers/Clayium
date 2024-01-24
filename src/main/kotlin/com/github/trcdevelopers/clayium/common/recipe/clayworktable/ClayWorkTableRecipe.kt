package com.github.trcdevelopers.clayium.common.recipe.clayworktable

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

class ClayWorkTableRecipe(
    input: ItemStack,
    primaryOutput: ItemStack,
    secondaryOutput: ItemStack,
    private val method: ClayWorkTableMethod,
    clicks: Int) {
    val input: ItemStack
        get() = field.copy()
    val primaryOutput: ItemStack
        get() = field.copy()
    val secondaryOutput: ItemStack
        get() = field.copy()
    val clicks: Int

    init {
        if (clicks < 1) {
            Clayium.LOGGER.warn("ClayWorkTable Recipe must have positive integer in clicks but got {}. Correcting it to 1.", clicks)
            this.clicks = 1
        } else {
            this.clicks = clicks
        }
        this.input = input
        this.primaryOutput = primaryOutput
        this.secondaryOutput = secondaryOutput
    }

    constructor(input: ItemStack, primaryOutput: ItemStack, method: ClayWorkTableMethod, clicks: Int) : this(
        input,
        primaryOutput,
        ItemStack.EMPTY,
        method,
        clicks
    )

    fun hasSecondaryOutput(): Boolean {
        return !secondaryOutput.isEmpty
    }

    fun matches(input: ItemStack, method: ClayWorkTableMethod): Boolean {
        return this.method === method && this.input.isItemEqual(input) && this.input.count <= input.count
    }

    fun canOutputPrimary(target: Slot): Boolean {
        return target.hasStack || primaryOutput.count + target.stack.count <= target.slotStackLimit
    }
    fun canOutputSecondary(target: Slot): Boolean {
        return target.hasStack || secondaryOutput.count + target.stack.count <= target.slotStackLimit
    }

    fun canOutputPrimary(target: ItemStack): Boolean {
        return target.isEmpty || primaryOutput.count + target.count <= target.maxStackSize
    }
    fun canOutputSecondary(target: ItemStack): Boolean {
        return target.isEmpty || secondaryOutput.count + target.count <= target.maxStackSize
    }
}
