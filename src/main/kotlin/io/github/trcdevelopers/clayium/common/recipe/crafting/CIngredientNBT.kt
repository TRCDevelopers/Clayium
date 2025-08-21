package io.github.trcdevelopers.clayium.common.recipe.crafting

import net.minecraft.item.ItemStack
import net.minecraftforge.common.crafting.IngredientNBT

class CIngredientNBT(stack: ItemStack) : IngredientNBT(stack) {
    // IngredientNBT.init is protected. This is the reason why this empty class exists.
}