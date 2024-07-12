package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.api.util.clayiumId
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import com.github.trcdevelopers.clayium.common.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.oredict.ShapedOreRecipe
import net.minecraftforge.oredict.ShapelessOreRecipe

object RecipeUtils {

    fun addSmeltingRecipe(input: ItemStack, output: ItemStack, exp: Float = 0f) {
        if (input.isEmpty) return logInvalidRecipe("Furnace recipe input cannot be empty.")
        if (output.isEmpty) return logInvalidRecipe("Furnace recipe output cannot be empty.")
        val recipes = FurnaceRecipes.instance()
        if (!recipes.getSmeltingResult(input).isEmpty) {
            return logInvalidRecipe("Furnace recipe for $input already exists.")
        }
        recipes.addSmeltingRecipe(input, output, exp)
    }

    fun addShapedRecipe(registryName: String, result: ItemStack, vararg recipe: Any) {
        addShapedRecipe(registryName, result, false, *recipe)
    }

    fun addShapedRecipe(registryName: String, result: ItemStack, isMirrored: Boolean, vararg recipe: Any) {
        val registryName = clayiumId(registryName)
        if (validateRecipe(registryName, result, recipe)) {
            ForgeRegistries.RECIPES.register(
                ShapedOreRecipe(null, result, *finalizeRecipe(recipe))
                    .setMirrored(isMirrored)
                    .setRegistryName(registryName))
        }
    }

    fun addShapelessRecipe(registryName: String, result: ItemStack, vararg recipe: Any) {
        ForgeRegistries.RECIPES.register(ShapelessOreRecipe(null, result, *finalizeRecipe(recipe))
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

    private fun validateRecipe(registryName: ResourceLocation, result: ItemStack, recipe: Array<out Any>): Boolean {
        if (ForgeRegistries.RECIPES.containsKey(registryName)) {
            logInvalidRecipe("Recipe $registryName already exists."); return false
        } else if (recipe.isEmpty()) {
            logInvalidRecipe("Recipe $registryName has no ingredients."); return false
        } else if (result.isEmpty) {
            logInvalidRecipe("Recipe $registryName has an empty result."); return false
        }

        return true
    }

    fun logInvalidRecipe(msg: String) {
        Clayium.LOGGER.warn("Invalid Recipe Found.", IllegalArgumentException(msg))
    }
}