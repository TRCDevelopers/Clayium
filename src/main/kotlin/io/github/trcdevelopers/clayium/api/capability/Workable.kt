package io.github.trcdevelopers.clayium.api.capability

import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widgets.ProgressWidget
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.recipe.RecipeLifecycleHandler
import io.github.trcdevelopers.clayium.api.util.toList
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.recipe.RecipeProgressTracker
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

open class Workable(
    metaTileEntity: MetaTileEntity,
    private val progressTracker: RecipeProgressTracker,
    private val lifecycleHandler: RecipeLifecycleHandler,
) : MTETrait(metaTileEntity, "TODO_REPLACE_ME") {

    open fun getTier(): Int {
        return this.metaTileEntity.tier.numeric
    }

    override fun update() {
        super.update()
        if (this.metaTileEntity.isRemote) return

        if (!this.progressTracker.isProcessingRecipe) {
            val newJob = this.lifecycleHandler.tryStartCrafting(this.getTier(), this.metaTileEntity.importItems.toList())
            if (newJob != null) {
                this.progressTracker.startProcessing(newJob)
            }
        }

        this.progressTracker.updateServer()
        if (this.progressTracker.isCompleted()) {
            this.lifecycleHandler.completeCrafting()
            this.progressTracker.reset()
        }
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.CONTROLLABLE -> capability.cast(this.progressTracker)
            else -> super.getCapability(capability, facing)
        }
    }

    fun getProgressBar(syncManager: PanelSyncManager, showRecipes: Boolean = true): ProgressWidget {
        this.progressTracker.syncProgressGui(syncManager)

        val widget = ProgressWidget()
            .size(22, 17)
            .progress(this.progressTracker::getNormalizedProgress)
            .texture(ClayGuiTextures.PROGRESS_BAR, 22)

        return widget
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = super.serializeNBT()
        nbt.setTag("progressTracker", this.progressTracker.serializeNBT())
        nbt.setTag("lifecycleHandler", this.lifecycleHandler.serializeNBT())
        return nbt
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        this.progressTracker.deserializeNBT(data.getCompoundTag("progressTracker"))
        this.lifecycleHandler.deserializeNBT(data.getCompoundTag("lifecycleHandler"))
    }
}