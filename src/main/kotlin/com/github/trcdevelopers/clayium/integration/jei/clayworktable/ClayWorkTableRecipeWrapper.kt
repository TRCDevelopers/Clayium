package com.github.trcdevelopers.clayium.integration.jei.clayworktable

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import com.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import com.github.trcdevelopers.clayium.integration.jei.JeiPlugin
import mezz.jei.api.gui.IDrawableAnimated
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

class ClayWorkTableRecipeWrapper(
    val recipe: ClayWorkTableRecipe
) : IRecipeWrapper {

    private val progressBar = guiHelper.createAnimatedDrawable(
        guiHelper.createDrawable(ResourceLocation(Clayium.MOD_ID, "textures/gui/clayworktable.png"), 176, 0, 80, 16),
        recipe.clicks * 5,
        IDrawableAnimated.StartDirection.LEFT,
        false
    )

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, listOf(recipe.input.inputStacks, recipe.method.requiredTools.map { ItemStack(it) }))
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs)
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        GlStateManager.pushMatrix()
        progressBar.draw(minecraft, 44, 23)
        for (id in ClayWorkTableMethod.ids) {
            if (id == recipe.method.id) {
                val cStr = recipe.clicks.toString()
                minecraft.fontRenderer.drawString(cStr, 41 + 16*id - 5 + 8 - minecraft.fontRenderer.getStringWidth(cStr) / 2, 36, 0)
                GlStateManager.color(1f, 1f, 1f, 1f)
                buttons[id + 6].draw(minecraft, 36 + 16*id, 46)
            } else {
                buttons[id].draw(minecraft, 36 + 16*id, 46)
            }
        }
        GlStateManager.popMatrix()
    }

    companion object {
        private val GUI_IMAGE = ResourceLocation(Clayium.MOD_ID, "textures/gui/clayworktable.png")
        private val guiHelper = JeiPlugin.jeiHelpers.guiHelper

        /**
         * 0-5 are disabled buttons, 6-11 are enabled buttons
         */
        private val buttons = listOf(List(ClayWorkTableMethod.entries.size) { i ->
            if (i == 5)
                guiHelper.createDrawable(GUI_IMAGE, 176, 80, 16, 16)
            else
                guiHelper.createDrawable(GUI_IMAGE, 176 + i*16, 32, 16, 16)
        },
        List(ClayWorkTableMethod.entries.size) { i ->
            if (i == 5)
                guiHelper.createDrawable(GUI_IMAGE, 176, 96, 16, 16)
            else
                guiHelper.createDrawable(GUI_IMAGE, 176 + i*16, 48, 16, 16)
        }).flatten()
    }
}