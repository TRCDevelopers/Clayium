package com.github.trc.clayium.api.pan

import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.recipe.ingredient.CRecipeInput
import net.minecraft.item.ItemStack
import java.util.function.Predicate

interface IPanEntry {
    val ingredients: List<CRecipeInput>
    val results: List<ItemStack>
    val requiredClayEnergy: ClayEnergy
}