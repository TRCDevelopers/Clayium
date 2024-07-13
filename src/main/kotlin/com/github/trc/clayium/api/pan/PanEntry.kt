package com.github.trc.clayium.api.pan

import com.github.trc.clayium.common.clayenergy.ClayEnergy
import net.minecraft.item.ItemStack
import java.util.function.Predicate

data class PanEntry(
    override val ingredients: List<Predicate<ItemStack>>,
    override val results: List<ItemStack>,
    override val requiredClayEnergy: ClayEnergy
) : IPanEntry
