package io.github.trcdevelopers.clayium.api.recipe

import net.minecraftforge.items.IItemHandler

data class RecipeInventoryContext(
    val itemHandler: IItemHandler,
)