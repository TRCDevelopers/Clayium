package com.github.trcdevelopers.clayium.integration.jei

import com.github.trcdevelopers.clayium.common.recipe.clayworktable.ClayWorkTableRecipe
import com.github.trcdevelopers.clayium.common.recipe.clayworktable.ClayWorkTableRecipeManager
import com.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeCategory
import com.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeWrapper
import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration

@JEIPlugin
class JeiPlugin : IModPlugin {

    lateinit var jeiRuntime: IJeiRuntime

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        this.jeiRuntime = jeiRuntime
    }

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        registry.addRecipeCategories(ClayWorkTableRecipeCategory(registry.jeiHelpers.guiHelper))
    }

    override fun register(registry: IModRegistry) {
        registry.handleRecipes(ClayWorkTableRecipe::class.java, ::ClayWorkTableRecipeWrapper, ClayWorkTableRecipeCategory.UID)

        registry.addRecipes(ClayWorkTableRecipeManager.INSTANCE.recipes, ClayWorkTableRecipeCategory.UID)
    }
}