package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.sync.ClayiumSyncManager

class SolarRecipeProcessor(
    syncManager: ClayiumSyncManager,
    metaTileEntity: MetaTileEntity,
) : RecipeProgressTracker(syncManager, metaTileEntity) {
}