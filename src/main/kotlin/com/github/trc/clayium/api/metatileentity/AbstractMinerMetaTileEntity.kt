package com.github.trc.clayium.api.metatileentity

import codechicken.lib.vec.Cuboid6
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.BoolValue
import com.cleanroommc.modularui.value.EnumValue
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.ToggleButton
import com.cleanroommc.modularui.widgets.layout.Grid
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.LaserEnergyHolder
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.client.model.ModelTextures
import com.github.trc.clayium.client.renderer.AreaMarkerRenderer
import com.github.trc.clayium.client.renderer.AreaMarkerRenderer.RangeRenderMode
import com.github.trc.clayium.common.gui.ClayGuiTextures
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityBeacon
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Function
import kotlin.math.log10

//todo refactor
abstract class AbstractMinerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    name: String,
    validInputModes: List<MachineIoMode> = validInputModesLists[0],
    validOutputModes: List<MachineIoMode> = validOutputModesLists[1],
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, name) {

    override val itemInventory = ClayiumItemStackHandler(this, INV_ROW * INV_COLUMN)
    override val importItems = EmptyItemStackHandler
    override val exportItems = itemInventory

    protected val laserEnergyHolder: LaserEnergyHolder = LaserEnergyHolder(this)

    protected var progress = 0.0
    private var workingEnabled = true

    private var rangeRenderMode = RangeRenderMode.DISABLED

    /**
     * used for rendering.
     * null for disable range rendering.
     */
    abstract val rangeRelative: Cuboid6?

    override fun update() {
        super.update()
        if (isRemote || !workingEnabled) return
        mineBlocks()
    }

    abstract fun mineBlocks()

    protected open fun addProgress() {
        progress += PROGRESS_PER_TICK_BASE * getAccelerationRate()
        laserEnergyHolder.drawAll()
    }

    protected fun getAccelerationRate(): Double {
        // actual $$r = 1 + 4 * log10(energy / 1000 + 1)$$
        val energy = laserEnergyHolder.storedPower.energy
        return 1 + 4 * log10(energy / 1000 + 1)
    }

    protected fun getRequiredProgress(blockHardness: Float): Double {
        return REQUIRED_PROGRESS_BASE * (0.1 + blockHardness)
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        syncManager.registerSlotGroup("breaker_inv", INV_ROW)
        val workingEnabledSync = SyncHandlers.bool(::workingEnabled, ::workingEnabled::set)
        syncManager.syncValue("working_enabled", workingEnabledSync)
        val columnStr = "I".repeat(INV_COLUMN)
        val matrixStr = (0..<INV_ROW).map { columnStr }

        val startButton = ToggleButton()
            .value(BoolValue.Dynamic(workingEnabledSync::getValue) { workingEnabledSync.value = true })
            .background(ClayGuiTextures.START_BUTTON)
            .hoverBackground(ClayGuiTextures.START_BUTTON_HOVERED)
            .selectedBackground(ClayGuiTextures.START_BUTTON_DISABLED)
        val stopButton = ToggleButton()
            .value(BoolValue.Dynamic({ !workingEnabledSync.value }, { workingEnabledSync.value = false }))
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
        val resetButton = ButtonWidget()
            .background(ClayGuiTextures.RESET)
            .hoverBackground(ClayGuiTextures.RESET_HOVERED)

        return super.buildMainParentWidget(syncManager)
            .child(Grid().coverChildren()
                .row(startButton, stopButton)
                .row(displayRange, resetButton)
                .minElementMargin(1, 1)
                .left(4).top(12)
            )
            .child(SlotGroupWidget.builder()
                .matrix(*matrixStr.toTypedArray())
                .key('I') { ItemSlot().slot(SyncHandlers.itemSlot(itemInventory, it).slotGroup("breaker_inv")) }
                .build().alignX(Alignment.TopCenter.x).top(12)
            )
            .child(laserEnergyHolder.createLpTextWidget(syncManager)
                .alignX(Alignment.Center.x).bottom(12)
            )
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("breaker", 176, 186)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setBoolean("workingEnabled", workingEnabled)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        workingEnabled = data.getBoolean("workingEnabled")
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val atlas = getter.apply(clayiumId("blocks/miner_back"))
        MINER_BACK = EnumFacing.entries.map { ModelTextures.createQuad(it, atlas) }
    }

    @SideOnly(Side.CLIENT)
    override fun getMaxRenderDistanceSquared() = Double.POSITIVE_INFINITY

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox() = TileEntityBeacon.INFINITE_EXTENT_AABB
    @SideOnly(Side.CLIENT)
    override fun useGlobalRenderer() = true
    @SideOnly(Side.CLIENT)
    override fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {
        AreaMarkerRenderer.render(Cuboid6.full, rangeRelative, x, y, z, rangeRenderMode)
    }

    companion object {
        private const val INV_ROW = 3
        private const val INV_COLUMN = 3

        const val PROGRESS_PER_TICK_BASE = 100
        const val REQUIRED_PROGRESS_BASE = 400

        @JvmStatic // for protected visibility
        protected lateinit var MINER_BACK: List<BakedQuad>
    }
}