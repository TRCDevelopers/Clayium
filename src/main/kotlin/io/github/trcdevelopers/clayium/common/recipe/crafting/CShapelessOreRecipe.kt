package io.github.trcdevelopers.clayium.common.recipe.crafting

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.oredict.ShapelessOreRecipe

class CShapelessOreRecipe(
    registryName: ResourceLocation,
    result: ItemStack,
    nbtSensitive: Boolean,
    vararg recipe: Any,
) : ShapelessOreRecipe(registryName, result, recipe) {
    init {

    }
}