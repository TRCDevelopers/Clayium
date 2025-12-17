package io.github.trcdevelopers.clayium.common.recipe

import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import io.github.trcdevelopers.clayium.api.capability.IWorkingControllable
import io.github.trcdevelopers.clayium.api.metatileentity.trait.OverclockHandler
import io.github.trcdevelopers.clayium.api.recipe.RecipeProcessingJob
import io.github.trcdevelopers.clayium.api.sync.ClayiumSyncManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

open class RecipeProgressTracker(
    syncManager: ClayiumSyncManager,
    private val ocHandler: OverclockHandler,
) : IWorkingControllable, INBTSerializable<NBTTagCompound> {

    private var state by syncManager.enum(State::class.java, State.IDLE)
    private var requiredProgress = 0L
    private var currentProgress = 0L

    val isProcessingRecipe get() = currentProgress != 0L

    open fun startProcessing(job: RecipeProcessingJob) {
        this.requiredProgress = job.requiredWork
        this.currentProgress = 1L
        this.state = State.WORKING
    }

    fun updateServer() {
        if (state == State.DISABLED) return

        if (isProcessingRecipe) {
            this.updateProgress()
        }
    }

    protected open fun updateProgress() {
        val rawProgress = this.getProgressPerTick()
        this.currentProgress += (rawProgress.toDouble() * ocHandler.accelerationFactor).toLong()
    }

    open fun isCompleted(): Boolean {
        return this.currentProgress > this.requiredProgress
    }

    open fun reset() {
        this.requiredProgress = 0L
        this.currentProgress = 0L
        this.state = State.IDLE
    }

    override val isWorking: Boolean get() = this.state == State.WORKING
    override var isWorkingEnabled: Boolean
        get() = this.state == State.DISABLED
        set(value) {
            if (value) {
                if (isProcessingRecipe) {
                    this.state = State.WORKING
                } else {
                    this.state = State.IDLE
                }
            } else {
                this.state = State.DISABLED
            }
        }

    protected open fun getProgressPerTick(): Long {
        return 1L
    }

    fun getNormalizedProgress(): Double {
        if (currentProgress == 0L || requiredProgress == 0L) return 0.0
        return (currentProgress.toDouble() - 1.0) / requiredProgress.toDouble()
    }

    fun syncProgressGui(pSyncManager: PanelSyncManager) {
        pSyncManager.syncValue("requiredProgress", SyncHandlers.longNumber(::requiredProgress, ::requiredProgress::set))
        pSyncManager.syncValue("craftingProgress", SyncHandlers.longNumber(::currentProgress, ::currentProgress::set))
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()
        nbt.setLong("requiredProgress", this.requiredProgress)
        nbt.setLong("currentProgress", this.currentProgress)
        nbt.setInteger("state", this.state.ordinal)
        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.requiredProgress = nbt.getLong("requiredProgress")
        this.currentProgress = nbt.getLong("currentProgress")
        this.state = State.entries.getOrNull(nbt.getInteger("state")) ?: State.IDLE
    }

    enum class State {
        IDLE, WORKING, DISABLED
    }
}