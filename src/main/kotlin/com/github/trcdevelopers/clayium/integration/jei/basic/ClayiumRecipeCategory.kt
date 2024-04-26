package com.github.trcdevelopers.clayium.integration.jei.basic

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.recipe.RecipeCategory
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IDrawableAnimated
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation

class ClayiumRecipeCategory(
    private val guiHelper: IGuiHelper,
    private val clayiumCategory: RecipeCategory,
) : IRecipeCategory<ClayiumRecipeWrapper> {

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
        return clayiumCategory.uniqueId
    }

    override fun getTitle(): String {
        // pass an empty string to non-prefixed machine name
        return I18n.format(clayiumCategory.translationKey, "")
    }

    override fun getModName(): String {
        return Clayium.MOD_NAME
    }

    override fun getBackground(): IDrawable {
        return this.background
    }

    override fun setRecipe(
        recipeLayout: IRecipeLayout,
        recipeWrapper: ClayiumRecipeWrapper,
        ingredients: IIngredients
    ) {

        val stacks = recipeLayout.itemStacks
        val inputs = ingredients.getInputs(VanillaTypes.ITEM)
        inputs.forEachIndexed { i, input ->
            stacks.init(i, true, 51 - i * 17, 20)
            stacks.set(i, input)
        }
        ingredients.getOutputs(VanillaTypes.ITEM).forEachIndexed { i, output ->
            stacks.init(i + inputs.size, false, 97 + i * 17, 20)
            stacks.set(i + inputs.size, output)

        }
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressBarBackground.draw(minecraft, 71, 21)
        progressBar.draw(minecraft, 71, 21)
    }
}