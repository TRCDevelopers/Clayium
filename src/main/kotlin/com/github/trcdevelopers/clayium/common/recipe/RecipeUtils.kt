package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.api.util.clayiumId
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import com.github.trcdevelopers.clayium.common.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.oredict.ShapedOreRecipe

object RecipeUtils {

    fun addShapedRecipe(registryName: String, result: ItemStack, vararg recipe: Any) {
        addShapedRecipe(registryName, result, false, *recipe)
    }

    fun addShapedRecipe(registryName: String, result: ItemStack, isMirrored: Boolean, vararg recipe: Any) {
        ForgeRegistries.RECIPES.register(ShapedOreRecipe(null, result, *finalizeRecipe(recipe))
            .setMirrored(isMirrored)
            .setRegistryName(clayiumId(registryName)))
    }

    private fun finalizeRecipe(recipe: Array<out Any>): Array<out Any> {
        val arr = Array<Any>(recipe.size) {}
        for (i in 0..<recipe.size)  {
            arr[i] = finalizeIngredient(recipe[i])
        }
        return arr
    }

    private fun finalizeIngredient(ingredient: Any): Any {
        return when (ingredient) {
            is MetaItemClayium.MetaValueItem -> ingredient.getStackForm()
            is UnificationEntry -> ingredient.toString()
            else -> ingredient
        }
    }
}