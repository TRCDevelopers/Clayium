package com.github.trcdevelopers.clayium.integration.jei

import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.recipe.CWTRecipes
import com.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import com.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeCategory
import com.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeWrapper
import mezz.jei.api.IJeiHelpers
import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import net.minecraft.item.ItemStack

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
        jeiHelpers = registry.jeiHelpers
        registry.handleRecipes(ClayWorkTableRecipe::class.java, ::ClayWorkTableRecipeWrapper, ClayWorkTableRecipeCategory.UID)
        registry.addRecipeCatalyst(ItemStack(ClayiumBlocks.CLAY_WORK_TABLE), ClayWorkTableRecipeCategory.UID)
        registry.addRecipes(CWTRecipes.CLAY_WORK_TABLE.recipes, ClayWorkTableRecipeCategory.UID)
    }

    companion object {
        lateinit var jeiHelpers: IJeiHelpers
    }
}