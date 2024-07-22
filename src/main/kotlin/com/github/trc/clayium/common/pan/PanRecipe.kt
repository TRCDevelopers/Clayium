package com.github.trc.clayium.common.pan

import com.github.trc.clayium.api.pan.IPanRecipe
import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.recipe.ingredient.CRecipeInput
import net.minecraft.item.ItemStack

data class PanRecipe(
    override val ingredients: List<CRecipeInput>,
    override val results: List<ItemStack>,
    override val requiredClayEnergy: ClayEnergy
) : IPanRecipe
