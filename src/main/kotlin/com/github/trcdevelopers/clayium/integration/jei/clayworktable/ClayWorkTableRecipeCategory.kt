package com.github.trcdevelopers.clayium.integration.jei.clayworktable

import com.github.trcdevelopers.clayium.common.Clayium
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeCategory
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation

class ClayWorkTableRecipeCategory(
    guiHelper: IGuiHelper
) : IRecipeCategory<ClayWorkTableRecipeWrapper> {

    private val overlay = guiHelper.createDrawable(ResourceLocation(Clayium.MOD_ID, "textures/gui/clayworktable.png"), 0, 0, 176, 166)

    override fun getUid(): String {
        return UID
    }

    override fun getTitle(): String {
        return I18n.format("tile.clayium.clay_work_table.name")
    }

    override fun getModName(): String {
        return Clayium.MOD_NAME
    }

    override fun getBackground(): IDrawable {
        return overlay
    }

    override fun setRecipe(
        recipeLayout: IRecipeLayout,
        recipeWrapper: ClayWorkTableRecipeWrapper,
        ingredients: IIngredients
    ) {
        val itemStacks = recipeLayout.itemStacks
        itemStacks.init(0, true, 17, 30)
        itemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM)[0])

        itemStacks.init(2, false, 143, 30)
        itemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM)[0])
        itemStacks.init(3, false, 143, 55)
        itemStacks.set(3, ingredients.getOutputs(VanillaTypes.ITEM)[1])
    }

    companion object {
        const val UID = "clayium:clay_work_table"
    }
}