package io.github.trcdevelopers.clayium.api.capability

import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widgets.ProgressWidget
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.recipe.RecipeJobProvider
import io.github.trcdevelopers.clayium.api.util.toList
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.recipe.RecipeProgressTracker
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

open class Workable(
    metaTileEntity: MetaTileEntity,
    private val progressTracker: RecipeProgressTracker,
    private val jobProvider: RecipeJobProvider,
) : MTETrait(metaTileEntity, "TODO_REPLACE_ME") {

    private var itemOutputs: List<ItemStack> = emptyList()

    open fun getTier(): Int {
        return metaTileEntity.tier.numeric
    }

    override fun update() {
        super.update()
        if (metaTileEntity.isRemote) return

        if (!progressTracker.isProcessingRecipe) {
            val newJob = jobProvider.provide(this.getTier(), metaTileEntity.importItems.toList(), metaTileEntity.overclockHandler.compensatedFactor)
            if (newJob != null) {
                progressTracker.startProcessing(newJob)
                this.itemOutputs = newJob.outputs
            }
        }

        progressTracker.updateServer()
        if (progressTracker.isCompleted()) {
            progressTracker.reset()
            TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs)
            itemOutputs = emptyList()
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
//        if (showRecipes && Mods.JustEnoughItems.isModLoaded) {
//            widget.addTooltipLine(IKey.lang("jei.tooltip.show.recipes"))
//                .listenGuiAction(IGuiAction.MousePressed { _ ->
//                    if (!widget.isBelowMouse) return@MousePressed false
//                    showRecipesInJei()
//                    return@MousePressed true
//                })
//        }

        return widget
    }


    override fun serializeNBT(): NBTTagCompound {
        val nbt = super.serializeNBT()
        nbt.setTag("progressTracker", progressTracker.serializeNBT())
        return nbt
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        progressTracker.deserializeNBT(data.getCompoundTag("progressTracker"))
    }
}