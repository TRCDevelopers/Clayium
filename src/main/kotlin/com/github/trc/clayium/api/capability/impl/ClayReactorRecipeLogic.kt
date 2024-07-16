package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.multiblock.ClayReactorMetaTileEntity
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.util.TransferUtils

class ClayReactorRecipeLogic(
    private val clayReactor: ClayReactorMetaTileEntity,
) : MultiblockRecipeLogic(clayReactor, CRecipes.CLAY_REACTOR) {
    override fun updateRecipeProgress() {
        if (drawEnergy(recipeCEt)) {
            currentProgress++
            currentProgress += clayReactor.laser?.laserEnergy?.toLong() ?: 0L
        }
        if (currentProgress > requiredProgress) {
            currentProgress = 0
            TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs)
        }
    }
}