package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.sync.ClayiumSyncManager

class SolarRecipeProcessor(
    syncManager: ClayiumSyncManager,
    metaTileEntity: MetaTileEntity,
) : RecipeProgressTracker(syncManager, metaTileEntity) {

    constructor(metaTileEntity: MetaTileEntity) : this(metaTileEntity.clayiumSyncManager, metaTileEntity)

    override fun canProgress(): Boolean {
        val world = metaTileEntity.world ?: return false
        val pos = metaTileEntity.pos ?: return false
        return world.canSeeSky(pos.up())
    }
}