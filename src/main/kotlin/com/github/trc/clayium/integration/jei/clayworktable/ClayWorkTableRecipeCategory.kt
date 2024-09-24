package com.github.trc.clayium.integration.jei.clayworktable

import com.github.trc.clayium.api.MOD_NAME
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack

class ClayWorkTableRecipeCategory(
    guiHelper: IGuiHelper
) : IRecipeCategory<ClayWorkTableRecipeWrapper> {

    private val overlay = guiHelper.createDrawable(clayiumId("textures/gui/clayworktable.png"), 4, 6, 168, 74)
    private val icon = guiHelper.createDrawableIngredient(ItemStack(ClayiumBlocks.CLAY_WORK_TABLE))

    override fun getUid(): String {
        return UID
    }

    override fun getTitle(): String {
        return I18n.format("tile.clayium.clay_work_table.name")
    }

    override fun getModName(): String {
        return MOD_NAME
    }

    override fun getBackground(): IDrawable {
        return overlay
    }

    override fun getIcon(): IDrawable? {
        return icon
    }

    override fun drawExtras(minecraft: Minecraft) {
//        progressBar.draw(minecraft, 44, 23)
    }

    override fun setRecipe(
        recipeLayout: IRecipeLayout,
        recipeWrapper: ClayWorkTableRecipeWrapper,
        ingredients: IIngredients
    ) {
        val itemStacks = recipeLayout.itemStacks
        itemStacks.init(0, true, 12, 23)
        itemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM)[0])
        itemStacks.init(1, true, 75, 10)
        itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM)[1])

        itemStacks.init(2, false, 138, 23)
        itemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM)[0])
        itemStacks.init(3, false, 138, 48)
        itemStacks.set(3, ingredients.getOutputs(VanillaTypes.ITEM)[1])
    }

    companion object {
        const val UID = "clayium:clay_work_table"
    }
}