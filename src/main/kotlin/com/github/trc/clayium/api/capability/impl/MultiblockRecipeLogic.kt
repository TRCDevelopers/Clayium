package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockLogic
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry

open class MultiblockRecipeLogic(
    metaTileEntity: WorkableMetaTileEntity,
    recipeRegistry: RecipeRegistry<*>,
    private val multiblockLogic: MultiblockLogic,
) : RecipeLogicEnergy(metaTileEntity, recipeRegistry, metaTileEntity.clayEnergyHolder) {
    override fun getTier(): Int {
        return multiblockLogic.recipeLogicTier
    }

    override fun canProgress(): Boolean {
        return multiblockLogic.structureFormed
    }
}