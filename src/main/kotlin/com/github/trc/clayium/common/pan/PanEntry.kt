package com.github.trc.clayium.common.pan

import com.github.trc.clayium.api.pan.IPanEntry
import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.recipe.ingredient.CRecipeInput
import net.minecraft.item.ItemStack

data class PanEntry(
    override val ingredients: List<CRecipeInput>,
    override val results: List<ItemStack>,
    override val requiredClayEnergy: ClayEnergy
) : IPanEntry
