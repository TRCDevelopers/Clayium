package com.github.trc.clayium.api.item.filter

import com.github.trc.clayium.api.capability.IItemFilter
import net.minecraft.util.ResourceLocation
import java.util.function.Supplier

object ItemFilterRegistry {
    private val filters = mutableMapOf<ResourceLocation, Supplier<IItemFilter>>()

    fun register(id: ResourceLocation, filterSupplier: Supplier<IItemFilter>) {
        if (filters.containsKey(id)) {
            throw IllegalArgumentException("Item filter with ID $id is already registered.")
        }
        filters[id] = filterSupplier
    }

    fun get(id: ResourceLocation): Supplier<IItemFilter>? {
        return filters[id]
    }
}