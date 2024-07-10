package com.github.trcdevelopers.clayium.integration.jei

import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.metatileentity.SolarClayFabricatorMetaTileEntity
import com.github.trcdevelopers.clayium.common.recipe.CWTRecipes
import com.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.integration.jei.basic.ClayiumRecipeCategory
import com.github.trcdevelopers.clayium.integration.jei.basic.ClayiumRecipeWrapper
import com.github.trcdevelopers.clayium.integration.jei.basic.SolarClayFabricatorRecipeWrapper
import com.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeCategory
import com.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeWrapper
import mezz.jei.api.IJeiHelpers
import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.api.recipe.IRecipeWrapperFactory
import net.minecraft.item.ItemStack

@JEIPlugin
class JeiPlugin : IModPlugin {

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        JeiPlugin.jeiRuntime = jeiRuntime
    }

    override fun registerCategories(jeiRegistry: IRecipeCategoryRegistration) {
        val guiHelper = jeiRegistry.jeiHelpers.guiHelper
        jeiRegistry.addRecipeCategories(ClayWorkTableRecipeCategory(guiHelper))

        for (recipeRegistry in CRecipes.ALL_REGISTRIES.values) {
            jeiRegistry.addRecipeCategories(
                ClayiumRecipeCategory(guiHelper, recipeRegistry.category)
            )
        }
    }

    override fun register(modRegistry: IModRegistry) {
        jeiHelpers = modRegistry.jeiHelpers
        modRegistry.handleRecipes(ClayWorkTableRecipe::class.java, ::ClayWorkTableRecipeWrapper, ClayWorkTableRecipeCategory.UID)
        modRegistry.addRecipeCatalyst(ItemStack(ClayiumBlocks.CLAY_WORK_TABLE), ClayWorkTableRecipeCategory.UID)
        modRegistry.addRecipes(CWTRecipes.CLAY_WORK_TABLE.recipes, ClayWorkTableRecipeCategory.UID)

        for (recipeRegistry in CRecipes.ALL_REGISTRIES.values) {
            val specialWrapper = recipeWrappers[recipeRegistry.category.uniqueId]
            if (specialWrapper != null) {
                modRegistry.handleRecipes(Recipe::class.java, specialWrapper, recipeRegistry.category.uniqueId)
                modRegistry.addRecipes(recipeRegistry.getAllRecipes(), recipeRegistry.category.uniqueId)
                continue
            }
            modRegistry.handleRecipes(Recipe::class.java, ::ClayiumRecipeWrapper, recipeRegistry.category.uniqueId)
            modRegistry.addRecipes(recipeRegistry.getAllRecipes(), recipeRegistry.category.uniqueId)
        }

        for (metaTileEntity in ClayiumApi.MTE_REGISTRY) {
            when (metaTileEntity) {
                is WorkableMetaTileEntity ->
                    modRegistry.addRecipeCatalyst(metaTileEntity.getStackForm(), metaTileEntity.recipeRegistry.category.uniqueId)
                is SolarClayFabricatorMetaTileEntity ->
                    modRegistry.addRecipeCatalyst(metaTileEntity.getStackForm(), metaTileEntity.registry.category.uniqueId)
            }
        }
    }

    companion object {
        lateinit var jeiHelpers: IJeiHelpers
        lateinit var jeiRuntime: IJeiRuntime

        //UID -> IRecipeWrapper
        private val recipeWrappers = mutableMapOf<String, IRecipeWrapperFactory<Recipe>>()

        fun registerWrapper(recipeRegistry: RecipeRegistry<*>, wrapperFactory: IRecipeWrapperFactory<Recipe>) {
            recipeWrappers[recipeRegistry.category.uniqueId] = wrapperFactory
        }
    }
}