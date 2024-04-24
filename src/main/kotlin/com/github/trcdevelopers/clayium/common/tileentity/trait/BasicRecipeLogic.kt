package com.github.trcdevelopers.clayium.common.tileentity.trait

import com.cleanroommc.modularui.api.widget.IWidget
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.tileentity.TileEntityMachine
import com.github.trcdevelopers.clayium.common.util.CUtils
import com.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack

class BasicRecipeLogic(
    tileEntityMachine: TileEntityMachine,
    tier: Int,
    private val ceSlot: ClayEnergyHolder,
    private val recipeRegistry: RecipeRegistry<*>,
) : TETrait(tileEntityMachine, tier) {

    private val inputInventory = tileEntity.inputInventory

    private var previousRecipe: Recipe? = null
    private var recipeCEt = ClayEnergy.ZERO
    private var requiredProgress = 0
    private var currentProgress = 0
    // item stacks that will be produced when the recipe is done
    private var itemOutputs: List<ItemStack> = emptyList()

    private var invalidInputsForRecipes = false
    private var outputsFull = false

    override fun update() {
        if (tileEntity.world?.isRemote == true) return
        if (currentProgress != 0) {
            updateRecipeProgress()
        }
        if (currentProgress == 0 && shouldSearchNewRecipe()) {
            trySearchNewRecipe()
        }
    }

    private fun updateRecipeProgress() {
        if (ceSlot.drawEnergy(recipeCEt)) currentProgress++
        if (currentProgress > requiredProgress) {
            currentProgress = 0
            TransferUtils.insertToHandler(tileEntity.outputInventory, itemOutputs)
        }
    }

    private fun shouldSearchNewRecipe(): Boolean {
        return canWorkWithInputs() && canFitNewOutputs()
    }

    private fun canWorkWithInputs(): Boolean {
        if (invalidInputsForRecipes && !tileEntity.hasNotifiedInputs) return false

        invalidInputsForRecipes = false
        tileEntity.hasNotifiedInputs = false
        return true
    }

    private fun canFitNewOutputs(): Boolean {
        if (outputsFull && !tileEntity.hasNotifiedOutputs) return false

        outputsFull = false
        tileEntity.hasNotifiedOutputs = false
        return true
    }

    private fun trySearchNewRecipe() {
        var currentRecipe: Recipe? = null
        currentRecipe = if (previousRecipe?.matches(false, inputInventory, tier) == true) {
            previousRecipe
        } else {
            recipeRegistry.findRecipe(tier, CUtils.handlerToList(inputInventory))
        }

        invalidInputsForRecipes = (currentRecipe == null)
        if (invalidInputsForRecipes) return
        // inputs are valid
        // -> are outputs full? && has enough power?
    }

    private fun prepareRecipe(recipe: Recipe) {
        if (!this.ceSlot.hasEnoughEnergy(recipe.cePerTick)) return
        val outputLimit = tileEntity.outputInventory.slots
        val outputs = recipe.copyOutputs().subList(0, outputLimit - 1)
        if (!TransferUtils.insertToHandler(tileEntity.outputInventory, outputs, true)) {
            this.outputsFull = true
            return
        }
        this.itemOutputs = outputs
        this.recipeCEt = recipe.cePerTick
        this.requiredProgress = recipe.duration
        this.currentProgress = 1
        this.previousRecipe = recipe
    }

    fun getProgressBar(): IWidget {
        return TODO()
    }
}