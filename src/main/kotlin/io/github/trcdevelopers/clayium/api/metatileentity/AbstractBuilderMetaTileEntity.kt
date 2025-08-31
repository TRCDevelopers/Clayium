package io.github.trcdevelopers.clayium.api.metatileentity

import codechicken.lib.vec.Cuboid6
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.EnumValue
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.ToggleButton
import com.cleanroommc.modularui.widgets.layout.Grid
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_HEIGHT
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_WIDTH
import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.capability.impl.EmptyItemStackHandler
import io.github.trcdevelopers.clayium.api.gui.data.MetaTileEntityGuiData
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.client.model.ModelTextures
import io.github.trcdevelopers.clayium.client.renderer.AreaMarkerRenderer
import io.github.trcdevelopers.clayium.client.renderer.AreaMarkerRenderer.RangeRenderMode
import io.github.trcdevelopers.clayium.common.config.ConfigCore
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Function

abstract class AbstractBuilderMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    name: String,
    validInputModes: List<MachineIoMode> = validInputModesLists[0],
    validOutputModes: List<MachineIoMode> = validOutputModesLists[1],
    val renderMinerBack: Boolean,
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, name) {

    override val itemInventory = ClayiumItemStackHandler(this, 3 * 3)
    override val importItems = EmptyItemStackHandler
    override val exportItems = itemInventory

    protected var workingEnabled = true
    protected var repeatEnabled = true

    protected var progress = 0.0

    var rangeRenderMode = RangeRenderMode.DISABLED

    /**
     * Called every tick. [accelerationRate] is obtained from [getAccelerationRate] method.
     */
    abstract fun drawEnergy(accelerationRate: Double): Boolean

    /**
     * Returns the next block position to work on.
     * Maybe called multiple times per tick.
     */
    abstract fun getNextBlockPos(): BlockPos?

    /**
     * used for rendering ONLY.
     * null for disable range rendering.
     *
     * This value is invalid on the server side.
     */
    abstract val rangeRelativeClient: Cuboid6?

    abstract val maxBlocksPerTick: Int
    open val maxSearchBlockPerTick = ConfigCore.misc.builderMaxSearchBlocksPerTick

    private var currentPos: BlockPos? = null

    /**
     * [progress] is multiplied by this per tick.
     * Default: always 1.0
     */
    protected open fun getAccelerationRate(): Double = 1.0

    /**
     * [progress] is required to work on given block.
     * Default: always 400.0
     */
    protected open fun getRequiredProgress(state: IBlockState, world: World, pos: BlockPos): Double = 400.0

    override fun update() {
        super.update()

        if (isRemote || !workingEnabled) return
        val world = world ?: return
        val r = getAccelerationRate()
        if (!drawEnergy(r)) return
        progress += PROGRESS_PER_TICK_BASE * getAccelerationRate()

        var remainingBlocks = maxBlocksPerTick
        for (i in 0..<maxSearchBlockPerTick) {
            if (remainingBlocks <= 0) break
            val pos = this.currentPos ?: getNextBlockPos() ?: return
            this.currentPos = pos

            val state = world.getBlockState(pos)
            val requiredProgress = getRequiredProgress(state, world, pos)
            if (progress < requiredProgress) return
            val result = this.actionOnBlock(state, world, pos)
            when (result) {
                EnumActionResult.SUCCESS -> {
                    this.currentPos = null
                    progress -= requiredProgress
                    remainingBlocks--
                }
                EnumActionResult.PASS -> this.currentPos = null
                EnumActionResult.FAIL -> break
            }
        }

        // If all blocks are processed, reset progress.
        if (remainingBlocks <= 0) progress = 0.0
    }

    /**
     * NOTE: if all [maxBlocksPerTick] blocks are mined, [progress] will be reset.
     *
     * @return EnumActionResult.
     * - [EnumActionResult.SUCCESS] if the block is successfully processed.
     * [maxBlocksPerTick] is consumed (subtracted by 1). Then it will search for the next block.
     * - [EnumActionResult.PASS] if the block is air or something like that.
     * [maxBlocksPerTick] is not consumed, but then it will search for the next block.
     * Example: Air, Unbreakable block, etc.
     * - [EnumActionResult.FAIL] if the work should be stopped. It will skip further working on this tick.
     * The block tried to process is not consumed, and it will try to process the same block again on the next tick.
     * Example: Inventory full
     */
    protected abstract fun actionOnBlock(state: IBlockState, world: World, pos: BlockPos): EnumActionResult

    override fun onPlacement() {
        if (this.frontFacing.axis.isHorizontal) {
            this.setOutput(this.frontFacing.rotateY(), MachineIoMode.ALL)
            this.setOutput(this.frontFacing.rotateYCCW(), MachineIoMode.ALL)
        } else {
            this.setOutput(EnumFacing.WEST, MachineIoMode.ALL)
            this.setOutput(EnumFacing.EAST, MachineIoMode.ALL)
        }
        super.onPlacement()
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setBoolean("workingEnabled", workingEnabled)
        data.setBoolean("repeatEnabled", repeatEnabled)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        workingEnabled = data.getBoolean("workingEnabled")
        repeatEnabled = data.getBoolean("repeatEnabled")
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        syncManager.registerSlotGroup("builder_inventory", 3)
        val columnStr = "I".repeat(3)
        val matrixStr = (0..<3).map { columnStr }

        return super.buildMainParentWidget(syncManager)
            .child(this.createButtonGrid(syncManager)
                .minElementMargin(1, 1)
                .left(4).top(12)
            )
            .child(SlotGroupWidget.builder()
                .matrix(*matrixStr.toTypedArray())
                .key('I') { MuiSlots.itemSlotBuilder(itemInventory, it).slotGroup("builder_inventory").build() }
                .build().alignX(Alignment.TopCenter.x).top(12)
            )
    }

    protected open fun createButtonGrid(syncManager: PanelSyncManager): Grid {
        val startButton = ToggleButton()
            .value(SyncHandlers.bool(::workingEnabled, { workingEnabled = true }))
            .background(ClayGuiTextures.START_BUTTON)
            .hoverBackground(ClayGuiTextures.START_BUTTON_HOVERED)
            .selectedBackground(ClayGuiTextures.START_BUTTON_DISABLED)
        val stopButton = ToggleButton()
            .value(SyncHandlers.bool({ !workingEnabled }, { workingEnabled = false }))
            .background(ClayGuiTextures.STOP_BUTTON)
            .hoverBackground(ClayGuiTextures.STOP_BUTTON_HOVERED)
            .selectedBackground(ClayGuiTextures.STOP_BUTTON_DISABLED)
        val displayRange = CycleButtonWidget()
            .background(ClayGuiTextures.DISPLAY_RANGE)
            .hoverBackground(ClayGuiTextures.DISPLAY_RANGE_HOVERED)
            .length(3)
            .value(EnumValue.Dynamic(RangeRenderMode::class.java, ::rangeRenderMode, ::rangeRenderMode::set))
            .tooltip(0) { it.addLine(IKey.lang("gui.clayium.range_visualization_mode.disabled")) }
            .tooltip(1) { it.addLine(IKey.lang("gui.clayium.range_visualization_mode.enabled")) }
            .tooltip(2) { it.addLine(IKey.lang("gui.clayium.range_visualization_mode.enabled_xray")) }
        val repeatButton = ToggleButton()
            .value(SyncHandlers.bool(::repeatEnabled, ::repeatEnabled::set))
            .background(ClayGuiTextures.REPEAT)
            .hoverBackground(ClayGuiTextures.REPEAT_HOVERED)
            .selectedBackground(ClayGuiTextures.REPEAT_DISABLED)

        return Grid().coverChildren()
            .row(startButton, stopButton)
            .row(displayRange, repeatButton)
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: PanelSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("builder", GUI_DEFAULT_WIDTH, GUI_DEFAULT_HEIGHT + 20)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val atlas = getter.apply(clayiumId("blocks/miner_back"))
        MINER_BACK = EnumFacing.entries.map { ModelTextures.createQuad(it, atlas) }
    }

    @SideOnly(Side.CLIENT)
    override fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {
        if (rangeRenderMode == RangeRenderMode.DISABLED) return
        AreaMarkerRenderer.render(Cuboid6.full, rangeRelativeClient, x, y, z, rangeRenderMode)
    }

    @SideOnly(Side.CLIENT)
    override fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        super.overlayQuads(quads, state, side, rand)
        if (state == null || side == null || state !is IExtendedBlockState) return
        if (renderMinerBack && side == this.frontFacing.opposite) {
            quads.add(MINER_BACK[side.index])
        }
    }

    companion object {
        const val PROGRESS_PER_TICK_BASE = 100

        @JvmStatic // for protected visibility
        protected lateinit var MINER_BACK: List<BakedQuad>
    }
}