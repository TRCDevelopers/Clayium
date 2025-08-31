package io.github.trcdevelopers.clayium.api.capability.impl

import io.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.multiblock.MultiblockLogic
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry

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