package com.github.trcdevelopers.clayium.integration.jei.simplecerecipe

import com.github.trcdevelopers.clayium.common.Clayium
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IDrawableAnimated
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

class SimpleCeRecipeCategory(
    guiHelper: IGuiHelper
) : IRecipeCategory<SimpleCeRecipeWrapper> {

    private val background = guiHelper.createDrawable(
        ResourceLocation(Clayium.MOD_ID, "textures/gui/jei.png"),
        5, 11, 166, 65,
    )
    private val progressBarBackground = guiHelper.createDrawable(
        ResourceLocation(Clayium.MOD_ID, "textures/gui/progress_bar.png"),
        0, 0, 24, 17,
    )
    private val progressBar = guiHelper.createAnimatedDrawable(
        guiHelper.createDrawable(ResourceLocation(Clayium.MOD_ID, "textures/gui/progress_bar.png"), 0, 17, 24, 17),
        40, IDrawableAnimated.StartDirection.LEFT, false,
    )

    override fun getUid(): String {
        return UID
    }

    override fun getTitle(): String {
        //todo
        return "Simple CE Recipe"
    }

    override fun getModName(): String {
        return Clayium.MOD_NAME
    }

    override fun getBackground(): IDrawable {
        return background
    }

    override fun setRecipe(
        recipeLayout: IRecipeLayout,
        recipeWrapper: SimpleCeRecipeWrapper,
        ingredients: IIngredients
    ) {
        val stacks = recipeLayout.itemStacks
        Clayium.LOGGER.info("inputSize: ${recipeWrapper.inputSize}, outputSize: ${recipeWrapper.outputSize}")
        for (i in 0..<recipeWrapper.inputSize) {
            stacks.init(i, true, 51 - i * 17, 20)
            stacks.set(i, ingredients.getInputs(VanillaTypes.ITEM)[i])
        }
        for (i in 0..<recipeWrapper.outputSize) {
            stacks.init(i + recipeWrapper.inputSize, false, 97 + i * 17, 20)
            stacks.set(i + recipeWrapper.inputSize, ingredients.getOutputs(VanillaTypes.ITEM)[i])
        }
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressBarBackground.draw(minecraft, 71, 21)
        progressBar.draw(minecraft, 71, 21)
    }

    companion object {
        const val UID = "clayium.simple_ce_recipe"
    }
}