package io.github.trcdevelopers.clayium.integration.jei

import io.github.trcdevelopers.clayium.api.ClayiumApi
import io.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.client.gui.GuiClayWorkTable
import io.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import io.github.trcdevelopers.clayium.common.items.ClayiumItems
import io.github.trcdevelopers.clayium.common.metatileentities.SolarClayFabricatorMetaTileEntity
import io.github.trcdevelopers.clayium.common.recipe.CWTRecipes
import io.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import io.github.trcdevelopers.clayium.common.recipe.Recipe
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import io.github.trcdevelopers.clayium.integration.jei.basic.ClayiumRecipeCategory
import io.github.trcdevelopers.clayium.integration.jei.basic.ClayiumRecipeWrapper
import io.github.trcdevelopers.clayium.integration.jei.basic.MetalSeparatorRecipeWrapper
import io.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeCategory
import io.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeWrapper
import mezz.jei.api.IJeiHelpers
import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeCategoryRegistration
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
        modRegistry.addRecipeClickArea(GuiClayWorkTable::class.java, 78, 29, 20, 16, ClayWorkTableRecipeCategory.UID)

        for (recipeRegistry in CRecipes.ALL_REGISTRIES.values) {
            val specialWrapper = recipeWrappers[recipeRegistry.category.uniqueId]
            if (specialWrapper != null) {
                modRegistry.handleRecipes(Recipe::class.java, specialWrapper, recipeRegistry.category.uniqueId)
                modRegistry.addRecipes(recipeRegistry.getAllRecipes(), recipeRegistry.category.uniqueId)
            } else if (recipeRegistry === CRecipes.CHEMICAL_METAL_SEPARATOR) {
                // Add RecipeDrawable for every single chanced output.
                // todo better way to do this?
                modRegistry.handleRecipes(MetalSeparatorRecipeWrapper.RecipeData::class.java,
                    ::MetalSeparatorRecipeWrapper, recipeRegistry.category.uniqueId)
                modRegistry.addRecipes(recipeRegistry.getAllRecipes()
                    .filter { it.chancedOutputs != null }
                    .flatMap { it.chancedOutputs!!.chancedOutputs.mapIndexed { i, _ -> MetalSeparatorRecipeWrapper.RecipeData(it, i) } },
                    recipeRegistry.category.uniqueId)
            } else {
                modRegistry.handleRecipes(Recipe::class.java, ::ClayiumRecipeWrapper, recipeRegistry.category.uniqueId)
                modRegistry.addRecipes(recipeRegistry.getAllRecipes(), recipeRegistry.category.uniqueId)
            }
        }

        for (registry in ClayiumApi.mteManager.allRegistries()) {
            for (metaTileEntity in registry) {
                when (metaTileEntity) {
                    is WorkableMetaTileEntity -> modRegistry.addRecipeCatalyst(metaTileEntity.asStackForm(),
                        metaTileEntity.recipeRegistry.category.uniqueId)

                    is SolarClayFabricatorMetaTileEntity -> modRegistry.addRecipeCatalyst(metaTileEntity.asStackForm(),
                        metaTileEntity.registry.category.uniqueId)
                }
            }
        }
        this.registerIngredientInfo(modRegistry)
    }

    private fun registerIngredientInfo(modRegistry: IModRegistry) {
        modRegistry.addIngredientInfo(listOf(ItemStack(ClayiumBlocks.CLAY_TREE_SAPLING), ItemStack(ClayiumBlocks.CLAY_TREE_LOG), ItemStack(ClayiumBlocks.CLAY_TREE_LEAVES)),
            VanillaTypes.ITEM, "recipe.clayium.clay_tree.description")
        modRegistry.addIngredientInfo(ItemStack(ClayiumBlocks.QUARTZ_CRUCIBLE), VanillaTypes.ITEM, "recipe.clayium.quartz_crucible.description")
        modRegistry.addIngredientInfo(OreDictUnifier.get(OrePrefix.gem, CMaterials.pureAntimatter), VanillaTypes.ITEM, "recipe.clayium.pure_antimatter.description")

        modRegistry.addIngredientInfo(ItemStack(ClayiumItems.DAMAGE_VALUE_ITEM_FILTER), VanillaTypes.ITEM, "item.clayium.item_filter_damage_value.jei_description")
        modRegistry.addIngredientInfo(ItemStack(ClayiumItems.BLOCK_METADATA_ITEM_FILTER), VanillaTypes.ITEM, "item.clayium.item_filter_block_metadata.jei_description")
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