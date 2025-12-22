package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.capability.Workable
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry

object RecipeLogicFactory {
    fun recipeLogicEnergy(metaTileEntity: MetaTileEntity, recipeRegistry: RecipeRegistry<*>, clayEnergyHolder: ClayEnergyHolder): Workable {
        return Workable(
            metaTileEntity,
            RecipeProgressTrackerEnergy(metaTileEntity.clayiumSyncManager, metaTileEntity, clayEnergyHolder),
            RecipeLifecycleHandlerEnergy(metaTileEntity, recipeRegistry, clayEnergyHolder),
        )
    }
}