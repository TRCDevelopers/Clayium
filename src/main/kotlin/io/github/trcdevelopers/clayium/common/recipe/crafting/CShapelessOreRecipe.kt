package io.github.trcdevelopers.clayium.common.recipe.crafting

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.ShapelessOreRecipe

class CShapelessOreRecipe(
    result: ItemStack,
    nbtSensitive: Boolean,
    vararg recipe: Any,
) : ShapelessOreRecipe(null /* group is for recipe book */, result) {
    init {
        for (o in recipe) {
            val ing = ClayiumIngredientFactory.get(nbtSensitive, o)
            this.input.add(ing)
            this.isSimple = this.isSimple && ing.isSimple
        }
    }
}