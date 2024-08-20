package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import java.util.function.BooleanSupplier

open class MultiblockRecipeLogic(
    metaTileEntity: WorkableMetaTileEntity,
    recipeRegistry: RecipeRegistry<*>,
    private val structureFormed: BooleanSupplier,
) : RecipeLogicEnergy(metaTileEntity, recipeRegistry, metaTileEntity.clayEnergyHolder) {
    override fun canProgress(): Boolean {
        return structureFormed.asBoolean && super.canProgress()
    }
}