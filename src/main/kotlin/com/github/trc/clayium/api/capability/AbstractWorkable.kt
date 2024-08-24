package com.github.trc.clayium.api.capability

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

abstract class AbstractWorkable(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.RECIPE_LOGIC), IControllable {
    var requiredProgress = 0L
        protected set
    var currentProgress = 0L
        protected set

    protected val ocHandler = metaTileEntity.overclockHandler
    protected var invalidInputsForRecipes = false
    protected var outputsFull = false

    override val isWorking: Boolean get() = currentProgress != 0L
    override var isWorkingEnabled: Boolean = true
    private var canProgress = false

    // item stacks that will be produced when the recipe is done
    protected var itemOutputs: List<ItemStack> = emptyList()

    /**
     * try to search for a new recipe.
     * you should mutate [invalidInputsForRecipes] or [outputsFull] here.
     */
    protected abstract fun trySearchNewRecipe()

    /**
     * Show recipes in JEI.
     * Not called if Jei isn't loaded.
     */
    protected abstract fun showRecipesInJei()

    protected open fun getTier(): Int = metaTileEntity.tier.numeric

    override fun update() {
        if (metaTileEntity.isRemote || !isWorkingEnabled) return
        if (metaTileEntity.offsetTimer % 20 == 0L) {
            this.canProgress = canProgress()
        }
        if (!canProgress) return

        // if you updateProgress then searchRecipe, it practically increases recipe duration by 1 tick.
        // this is because when the (recipe output > half of the max stack size),
        // next recipe output cannot fit in the output slot and thus will not match.
        if (!isWorking && shouldSearchForRecipe()) {
            trySearchNewRecipe()
        }
        if (isWorking) {
            updateWorkingProgress()
        }
    }

    override fun onFirstTick() {
        super.onFirstTick()
        this.canProgress = canProgress()
    }

    /**
     * Called every second.
     * You can check some extra conditions like neighbouring blocks here.
     */
    protected open fun canProgress(): Boolean {
        return true
    }

    /**
     * Called every tick when the machine is working.
     * If you have to consume Energy or other resources, You should do it here.
     */
    protected open fun updateWorkingProgress() {
        currentProgress += (getProgressPerTick() * ocHandler.accelerationFactor).toLong()
        if (currentProgress >= requiredProgress) {
            completeWork()
        }
    }

    /**
     * returns the progress per tick without overclocking.
     * called every tick when the machine is working.
     */
    protected open fun getProgressPerTick(): Long {
        return 1
    }

    protected open fun completeWork() {
        currentProgress = 0
        TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs)
    }

    protected open fun shouldSearchForRecipe(): Boolean {
        return canWorkWithInputs() && canFitNewOutputs()
    }

    private fun canWorkWithInputs(): Boolean {
        if (invalidInputsForRecipes && !metaTileEntity.hasNotifiedInputs) return false

        invalidInputsForRecipes = false
        metaTileEntity.hasNotifiedInputs = false
        return true
    }

    private fun canFitNewOutputs(): Boolean {
        return true
        
        // currently, NotifiableItemStackHandler.onContentsChanged isn't called
        // if the item is extracted without pressing a shift key in GUI.
        // therefore, metaTileEntity.hasNotifiedOutputs is remains false in that case.
        // so output full check is disabled.

//        if (outputsFull && !metaTileEntity.hasNotifiedOutputs) return false
//
//        outputsFull = false
//        metaTileEntity.hasNotifiedOutputs = false
//        return true
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = super.serializeNBT()
        data.setLong("currentProgress", currentProgress)
        data.setLong("requiredProgress", requiredProgress)
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        currentProgress = data.getLong("currentProgress")
        requiredProgress = data.getLong("requiredProgress")
    }

    fun getProgressBar(syncManager: GuiSyncManager): ProgressWidget {
        syncManager.syncValue("requiredProgress", SyncHandlers.longNumber(::requiredProgress, ::requiredProgress::set))
        syncManager.syncValue("craftingProgress", SyncHandlers.longNumber(::currentProgress, ::currentProgress::set))

        val widget = ProgressWidget()
            .size(22, 17)
            .progress(this::getNormalizedProgress)
            .texture(ClayGuiTextures.PROGRESS_BAR, 22)
        if (Mods.JustEnoughItems.isModLoaded) {
            widget.addTooltipLine(IKey.lang("jei.tooltip.show.recipes"))
                .listenGuiAction(IGuiAction.MousePressed { _ ->
                    if (!widget.isBelowMouse) return@MousePressed false
                    showRecipesInJei()
                    return@MousePressed true
                })
        }

        return widget
    }

    fun getNormalizedProgress(): Double {
        if (currentProgress == 0L || requiredProgress == 0L) return 0.0
        return (currentProgress.toDouble() - 1.0) / requiredProgress.toDouble()
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ClayiumTileCapabilities.CONTROLLABLE) {
            capability.cast(this)
        } else {
            super.getCapability(capability, facing)
        }
    }
}