package com.github.trc.clayium.common.recipe.logic

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trc.clayium.api.recipe.IRecipeProvider
import com.github.trc.clayium.common.metatileentities.PanDuplicatorMetaTileEntity
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.util.TransferUtils
import kotlin.math.min

class PanDuplicatorRecipeLogic(
    val duplicator: PanDuplicatorMetaTileEntity,
    provider: IRecipeProvider,
    energyHolder: ClayEnergyHolder,
) : RecipeLogicEnergy(duplicator, provider, energyHolder) {
    private var requiredEnergyRemaining = ClayEnergy.ZERO

    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        val thisTickCeLong = min(ce.energy, requiredEnergyRemaining.energy)
        val thisTickCe = ClayEnergy(thisTickCeLong)
        val isDrawn = super.drawEnergy(thisTickCe, simulate)
        if (isDrawn && !simulate) {
            requiredEnergyRemaining -= thisTickCe
        }
        return isDrawn
    }

    // In this recipe logic, recipe.cePerTick means the required energy of the duplication.
    // The duration must be already calculated.
    override fun prepareRecipe(recipe: Recipe): Boolean {
        val thisTickCeLong = min(recipe.cePerTick.energy, duplicator.maxCeConsumptionRate.energy)
        val thisTickCe = ClayEnergy(thisTickCeLong)

        if (!this.drawEnergy(thisTickCe, simulate = true)) return false
        val outputs = recipe.copyOutputs().take(metaTileEntity.exportItems.slots)
        if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, outputs, true)) {
            this.outputsFull = true
            return false
        }
        if (!recipe.matches(true, inputInventory, getTier())) return false
        val (cePerTick, duration) = applyOverclock(recipe.cePerTick, recipe.duration, ocHandler.compensatedFactor)
        this.itemOutputs = outputs
        this.recipeCEt = thisTickCe
        this.requiredEnergyRemaining = recipe.cePerTick
        this.requiredProgress = duration
        this.currentProgress = 1
        this.previousRecipe = recipe
        return true
    }

    override fun completeWork() {
        this.requiredEnergyRemaining = ClayEnergy.ZERO
        super.completeWork()
    }
}