package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.metatileentity.trait.OverclockHandler
import io.github.trcdevelopers.clayium.api.recipe.RecipeProcessingJob
import io.github.trcdevelopers.clayium.api.sync.ClayiumSyncManager

class RecipeProgressTrackerEnergy(
    syncManager: ClayiumSyncManager,
    ocHandler: OverclockHandler,
    private val energyHolder: ClayEnergyHolder,
) : RecipeProgressTracker(syncManager, ocHandler) {
    private var cePerTick = ClayEnergy.ZERO

    fun drawEnergy(): Boolean {
        return this.energyHolder.drawEnergy(cePerTick, simulate = false)
    }

    override fun startProcessing(job: RecipeProcessingJob) {
        super.startProcessing(job)
        this.cePerTick = job.clayEnergyPerTick
    }

    override fun updateProgress() {
        if (this.drawEnergy()) {
            super.updateProgress()
        }
    }

    override fun reset() {
        super.reset()
        this.cePerTick = ClayEnergy.ZERO
    }
}