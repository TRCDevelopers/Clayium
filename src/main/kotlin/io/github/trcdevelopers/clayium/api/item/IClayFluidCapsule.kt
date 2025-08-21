package io.github.trcdevelopers.clayium.api.item

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

/**
 * Interface for items.
 */
interface IClayFluidCapsule {
    val capacity: Int
    fun getFluid(stack: ItemStack): FluidStack?
}