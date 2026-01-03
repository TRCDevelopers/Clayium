package io.github.trcdevelopers.clayium.api.capability

import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widgets.ProgressWidget
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.recipe.RecipeLifecycleHandler
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.api.util.toList
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.recipe.RecipeProgressTracker
import io.github.trcdevelopers.clayium.integration.theoneprobe.TheOneProbeModule.BORDER_COLOR
import io.github.trcdevelopers.clayium.integration.theoneprobe.TheOneProbeModule.COLOR_DISABLED_ARGB
import io.github.trcdevelopers.clayium.integration.theoneprobe.TheOneProbeModule.COLOR_ENABLED_ARGB
import io.github.trcdevelopers.clayium.integration.theoneprobe.TheOneProbeModule.HALF_HOUR_TICKS
import io.github.trcdevelopers.clayium.integration.theoneprobe.TheOneProbeModule.ONE_MIN_TICKS
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.NumberFormat
import mcjty.theoneprobe.api.ProbeMode
import mcjty.theoneprobe.api.TextStyleClass
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Optional
import kotlin.math.round

open class Workable<T: RecipeProgressTracker, R: RecipeLifecycleHandler>(
    metaTileEntity: MetaTileEntity,
    val progressTracker: T,
    val lifecycleHandler: R,
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
            capability === ClayiumTileCapabilities.WORKABLE -> capability.cast(this)
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

    /**
     * must be annotated with `@Optional.Method(modid = Mods.Names.THE_ONE_PROBE)`
     */
    @Optional.Method(modid = Mods.Names.THE_ONE_PROBE)
    open fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
        val isWorking = this.progressTracker.isWorking
        val isWorkingEnabled = this.progressTracker.isWorkingEnabled
        // machine is enabled but no recipe, just skip.
        if (!isWorking && isWorkingEnabled) return

        var current = this.progressTracker.currentProgress
        var required = this.progressTracker.requiredProgress

        val suffix = if (required > HALF_HOUR_TICKS) {
            current = round(current / ONE_MIN_TICKS).toLong()
            required = round(required / ONE_MIN_TICKS).toLong()
            " / $required min"
        } else if (required > 20) {
            current = round(current / 20.0).toLong()
            required = round(required / 20.0).toLong()
            " / $required s"
        } else {
            " / $required t"
        }

        val color = if (isWorkingEnabled) COLOR_ENABLED_ARGB else COLOR_DISABLED_ARGB
        if (required > 0) {
            probeInfo.progress(
                current, required, probeInfo.defaultProgressStyle()
                    .suffix(suffix)
                    .filledColor(color)
                    .alternateFilledColor(color)
                    .borderColor(BORDER_COLOR)
                    .numberFormat(NumberFormat.COMMAS)
            )
        }

        if (!isWorkingEnabled) {
            // machine is disabled, show paused message
            probeInfo.text("${TextStyleClass.WARNING}${IProbeInfo.STARTLOC}gui.clayium.working_paused${IProbeInfo.ENDLOC}")
        }
    }
}