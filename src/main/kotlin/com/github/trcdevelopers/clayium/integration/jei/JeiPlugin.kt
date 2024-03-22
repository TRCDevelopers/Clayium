package com.github.trcdevelopers.clayium.integration.jei

import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.recipe.CRecipes
import com.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import com.github.trcdevelopers.clayium.common.recipe.SimpleCeRecipe
import com.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeCategory
import com.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeWrapper
import com.github.trcdevelopers.clayium.integration.jei.simplecerecipe.SimpleCeRecipeCategory
import com.github.trcdevelopers.clayium.integration.jei.simplecerecipe.SimpleCeRecipeWrapper
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
        registry.addRecipeCategories(SimpleCeRecipeCategory(registry.jeiHelpers.guiHelper, SimpleCeRecipeCategory.BENDING))
    }

    override fun register(registry: IModRegistry) {
        jeiHelpers = registry.jeiHelpers
        registry.handleRecipes(ClayWorkTableRecipe::class.java, ::ClayWorkTableRecipeWrapper, ClayWorkTableRecipeCategory.UID)
        registry.addRecipeCatalyst(ItemStack(ClayiumBlocks.CLAY_WORK_TABLE), ClayWorkTableRecipeCategory.UID)
        registry.addRecipes(CRecipes.CLAY_WORK_TABLE.recipes, ClayWorkTableRecipeCategory.UID)

        registry.handleRecipes(SimpleCeRecipe::class.java, ::SimpleCeRecipeWrapper, SimpleCeRecipeCategory.BENDING)
        MachineBlocks.BENDING_MACHINE.forEach { registry.addRecipeCatalyst(ItemStack(it.value), SimpleCeRecipeCategory.BENDING) }
        registry.addRecipes(CRecipes.BENDING.recipes, SimpleCeRecipeCategory.BENDING)
    }

    companion object {
        lateinit var jeiHelpers: IJeiHelpers
    }
}