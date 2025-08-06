package io.github.trcdevelopers.clayium.api.capability

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ProgressWidget
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.WORKABLE_STATE
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.NumberFormat
import mcjty.theoneprobe.api.ProbeMode
import mcjty.theoneprobe.api.TextStyleClass
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Optional
import kotlin.math.round

//todo cleanup
abstract class AbstractWorkable(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.RECIPE_LOGIC), IWorkingControllable {

    var state: State = State.IDLE
        protected set(value) {
            val syncFlag = !metaTileEntity.isRemote && field != value
            if (syncFlag) {
                writeCustomData(WORKABLE_STATE) {
                    writeEnumValue(value)
                }
            }
            field = value
        }

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

    override val isWorking get() = this.state == State.WORKING
    override var isWorkingEnabled get() = this.state != State.DISABLED
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

    private var canProgress = false

    // item stacks that will be produced when the recipe is done
    protected var itemOutputs: List<ItemStack> = emptyList()

    /**
     * try to search for a new recipe.
     * you should mutate [invalidInputsForRecipes] or [outputsFull] here.
     *
     * @return true if a new recipe is found and ready to work, false otherwise.
     */
    protected abstract fun trySearchNewRecipe(): Boolean

    /**
     * Show recipes in JEI.
     * Not called if Jei isn't loaded.
     */
    protected open fun showRecipesInJei() {}

    protected open fun getTier(): Int = metaTileEntity.tier.numeric

    override fun update() {
        if (metaTileEntity.isRemote) return
        if (this.state == State.DISABLED) return

        if (metaTileEntity.offsetTimer % 20 == 0L) this.canProgress = canProgress()

        if (!canProgress) {
            this.state = State.IDLE
            return
        }

        // if you updateProgress then searchRecipe, it practically increases recipe duration by 1 tick.
        // this is because when the (recipe output > half of the max stack size),
        // next recipe output cannot fit in the output slot and thus will not match.
        if (!isProcessingRecipe && shouldSearchForRecipe()) {
            val readyToWork = trySearchNewRecipe()
            if (readyToWork) {
                this.state = State.WORKING
            } else {
                this.state = State.IDLE
            }
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

    protected fun canWorkWithInputs(): Boolean {
        if (invalidInputsForRecipes && !metaTileEntity.hasNotifiedInputs) return false

        invalidInputsForRecipes = false
        metaTileEntity.hasNotifiedInputs = false
        return true
    }

    protected fun canFitNewOutputs(): Boolean {
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

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        buf.writeEnumValue(state)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        super.receiveInitialSyncData(buf)
        this.state = buf.readEnumValue(State::class.java)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == WORKABLE_STATE) {
            this.state = buf.readEnumValue(State::class.java)
            return
        }
        super.receiveCustomData(discriminator, buf)
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

    fun getProgressBar(syncManager: PanelSyncManager, showRecipes: Boolean = true): ProgressWidget {
        syncManager.syncValue("requiredProgress", SyncHandlers.longNumber(::requiredProgress, ::requiredProgress::set))
        syncManager.syncValue("craftingProgress", SyncHandlers.longNumber(::currentProgress, ::currentProgress::set))

        val widget = ProgressWidget()
            .size(22, 17)
            .progress(this::getNormalizedProgress)
            .texture(ClayGuiTextures.PROGRESS_BAR, 22)
        if (showRecipes && Mods.JustEnoughItems.isModLoaded) {
            widget.addTooltipLine(IKey.lang("jei.tooltip.show.recipes"))
                .listenGuiAction(IGuiAction.MousePressed { _ ->
                    if (!widget.isBelowMouse) return@MousePressed false
                    showRecipesInJei()
                    return@MousePressed true
                })
        }

        return widget
    }

    open fun getNormalizedProgress(): Double {
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
    /**
     * must be annotated with `@Optional.Method(modid = Mods.Names.THE_ONE_PROBE)`
     */
    open fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
        // Explicitly Display PAUSED (Change the color of the progress bar) if not isWorkingEnabled
        if (!isWorking && isWorkingEnabled) return

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

        if (!isWorkingEnabled) {
            probeInfo.text("${TextStyleClass.WARNING}${IProbeInfo.STARTLOC}gui.clayium.working_paused${IProbeInfo.ENDLOC}")
        }
    }

    enum class State {
        IDLE, WORKING, DISABLED
    }
}

// TOP Info Colors
private const val COLOR_ENABLED_ARGB: Int = 0xFF4CBB17.toInt()
private const val COLOR_DISABLED_ARGB: Int = 0xFFBB1C28.toInt()
private const val BORDER_COLOR: Int = 0xFF555555.toInt()
private const val HALF_HOUR_TICKS: Int = 30 * 60 * 20
private const val ONE_MIN_TICKS: Double = 60 * 20.0
