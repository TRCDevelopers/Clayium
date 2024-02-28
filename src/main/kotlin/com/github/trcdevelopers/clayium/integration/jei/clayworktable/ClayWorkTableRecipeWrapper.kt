package com.github.trcdevelopers.clayium.integration.jei.clayworktable

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import com.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButtonImage
import net.minecraft.util.ResourceLocation

class ClayWorkTableRecipeWrapper(
    val recipe: ClayWorkTableRecipe
) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, listOf(recipe.input.inputStacks))
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs)
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        for (method in ClayWorkTableMethod.entries) {
            buttons[if (method == recipe.method) method.id + 6 else method.id].drawButton(minecraft, mouseX, mouseY, 0f)
        }
    }

    companion object {
        private val GUI_IMAGE = ResourceLocation(Clayium.MOD_ID, "textures/gui/clayworktable.png")

        /**
         * 0-5 are disabled buttons, 6-11 are enabled buttons
         */
        private val buttons = listOf(List(ClayWorkTableMethod.entries.size) { i ->
            if (i == 5)
                GuiButtonImage(i, 116, 46, 16, 16, 176, 80, 0, GUI_IMAGE)
            else
                GuiButtonImage(i, 36 + i*16, 46, 16, 16, 176 + i*16, 32, 0, GUI_IMAGE)
        },
        List(ClayWorkTableMethod.entries.size) { i ->
            if (i == 5)
                GuiButtonImage(i, 116, 46, 16, 16, 176, 96, 0, GUI_IMAGE)
            else
                GuiButtonImage(i, 36 + i*16, 46, 16, 16, 176 + i*16, 48, 0, GUI_IMAGE)
        }).flatten()
    }
}