package com.github.trc.clayium.api.capability

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.util.TransferUtils
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.NumberFormat
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Optional
import org.jetbrains.annotations.MustBeInvokedByOverriders
import kotlin.math.round

//todo cleanup
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

    /**
     * always false for 1 tick recipe. so it isn't used for Redstone Interface.
     */
    private val isProcessingRecipe: Boolean get() = currentProgress != 0L

    /**
     * used for Redstone Interfaces.
     * should be overridden if the machine has 1 tick recipe.
     */
    override val isWorking: Boolean get() = isProcessingRecipe
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
        if (!isProcessingRecipe && shouldSearchForRecipe()) {
            trySearchNewRecipe()
        }
        if (isProcessingRecipe) {
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
        CUtils.writeItems(itemOutputs, "itemOutputs", data)
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        currentProgress = data.getLong("currentProgress")
        requiredProgress = data.getLong("requiredProgress")
        itemOutputs = CUtils.readItems("itemOutputs", data)
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
        return when {
            capability === ClayiumTileCapabilities.CONTROLLABLE -> capability.cast(this)
            capability === ClayiumTileCapabilities.WORKABLE -> capability.cast(this)
            else -> super.getCapability(capability, facing)
        }
    }

    @Optional.Method(modid = Mods.Names.THE_ONE_PROBE)
    @MustBeInvokedByOverriders
    /**
     * must be annotated with `@Optional.Method(modid = Mods.Names.THE_ONE_PROBE)`
     */
    open fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
        if (!isWorking) return

        var progress = currentProgress
        var maxProgress = requiredProgress

        val suffix = if (maxProgress > HALF_HOUR_TICKS) {
            progress = round(progress / ONE_MIN_TICKS).toLong()
            maxProgress = round(maxProgress / ONE_MIN_TICKS).toLong()
            " / $maxProgress min"
        } else if (maxProgress > 20) {
            progress = round(progress / 20.0).toLong()
            maxProgress = round(maxProgress / 20.0).toLong()
            " / $maxProgress s"
        } else {
            " / $maxProgress t"
        }

        val color = if (isWorkingEnabled) COLOR_ENABLED_ARGB else COLOR_DISABLED_ARGB
        if (requiredProgress > 0) {
            probeInfo.progress(
                progress, maxProgress, probeInfo.defaultProgressStyle()
                    .suffix(suffix)
                    .filledColor(color)
                    .alternateFilledColor(color)
                    .borderColor(BORDER_COLOR)
                    .numberFormat(NumberFormat.COMMAS)
            )
        }
    }
}

// TOP Info Colors
private const val COLOR_ENABLED_ARGB: Int = 0xFF4CBB17.toInt()
private const val COLOR_DISABLED_ARGB: Int = 0xFFBB1C28.toInt()
private const val BORDER_COLOR: Int = 0xFF555555.toInt()
private const val HALF_HOUR_TICKS: Int = 30 * 60 * 20
private const val ONE_MIN_TICKS: Double = 60 * 20.0
