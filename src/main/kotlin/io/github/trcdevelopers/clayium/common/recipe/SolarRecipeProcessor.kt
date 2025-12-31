package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.recipe.RecipeProcessingJob
import io.github.trcdevelopers.clayium.api.sync.ClayiumSyncManager

class SolarRecipeProcessor(
    syncManager: ClayiumSyncManager,
    metaTileEntity: MetaTileEntity,
) : RecipeProgressTracker(syncManager, metaTileEntity) {

    private var ceGeneratedPerTick = ClayEnergy.ZERO

    /**
     * public for sync.
     */
    var clayEnergy = ClayEnergy.ZERO

    constructor(metaTileEntity: MetaTileEntity) : this(metaTileEntity.clayiumSyncManager, metaTileEntity)

    override fun startProcessing(job: RecipeProcessingJob) {
        super.startProcessing(job)
        this.ceGeneratedPerTick = job.clayEnergyPerTick
    }

    override fun updateProgress() {
        super.updateProgress()
        this.clayEnergy += this.ceGeneratedPerTick
    }

    override fun reset() {
        super.reset()
        this.ceGeneratedPerTick = ClayEnergy.ZERO
    }

    override fun canProgress(): Boolean {
        val world = metaTileEntity.world ?: return false
        val pos = metaTileEntity.pos ?: return false
        return world.canSeeSky(pos.up())
    }
}