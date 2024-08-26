package com.github.trc.clayium.api.metatileentity

import codechicken.lib.vec.Cuboid6
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
import com.github.trc.clayium.api.CValues
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
import com.github.trc.clayium.common.util.TransferUtils
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.I18n
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Function
import kotlin.math.ln

abstract class AbstractMinerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    private val machineName: String,
    validInputModes: List<MachineIoMode> = validInputModesLists[0],
    validOutputModes: List<MachineIoMode> = validOutputModesLists[1],
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, "machine.${CValues.MOD_ID}.$machineName") {

    override val itemInventory = ClayiumItemStackHandler(this, INV_ROW * INV_COLUMN)
    override val importItems = EmptyItemStackHandler
    override val exportItems = itemInventory

    private val laserEnergyHolder: LaserEnergyHolder = LaserEnergyHolder(this)

    protected var progress = 0.0
    private var currentTargetPos: BlockPos? = null
    private var workingEnabled = true

    @SideOnly(Side.CLIENT)
    private var rangeRenderMode = RangeRenderMode.DISABLED

    /**
     * next block pos to harvest. called if current block is broken.
     * null if no more block to harvest.
     */
    abstract fun getNextBlockPos(): BlockPos?

    /**
     * used for rendering.
     * null for disable range rendering.
     */
    abstract val rangeRelative: Cuboid6?

    override fun onFirstTick() {
        super.onFirstTick()
        currentTargetPos = getNextBlockPos()
    }

    override fun update() {
        //todo fortune, silk touch handling
        super.update()
        if (isRemote || !workingEnabled) return
        val world = world ?: return
        val targetPos = currentTargetPos ?: return

        val state = world.getBlockState(targetPos)
        val hardness = state.getBlockHardness(world, targetPos)

        if (hardness == CValues.HARDNESS_UNBREAKABLE) {
            currentTargetPos = getNextBlockPos()
            return
        }

        val requiredProgress = getRequiredProgress(hardness)
        if (progress < requiredProgress) {
            addProgress()
        }
        if (progress >= requiredProgress) {
            progress -= requiredProgress
            val drops = NonNullList.create<ItemStack>()
            state.block.getDrops(drops, world, targetPos, state, 0)
            if (TransferUtils.insertToHandler(itemInventory, drops, true)) {
                TransferUtils.insertToHandler(itemInventory, drops, false)
                world.destroyBlock(targetPos, false)
                currentTargetPos = getNextBlockPos()
            }
        }
    }

    protected open fun addProgress() {
        progress += 100.0 * getAccelerationRate()
        laserEnergyHolder.drawAll()
    }

    protected fun getAccelerationRate(): Double {
        val energy = laserEnergyHolder.storedPower.energy
        val a = energy / 1000.0 + 1
        val b = ln(10.0)
        return (1 + a / b)
    }

    private fun getRequiredProgress(blockHardness: Float): Double {
        return 400 * (0.1 + blockHardness)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("machines/$machineName"), "tier=${tier.lowerName}"))
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
            .tooltip(0) { it.addLine(I18n.format("gui.clayium.range_visualization_mode.disabled")) }
            .tooltip(1) { it.addLine(I18n.format("gui.clayium.range_visualization_mode.enabled")) }
            .tooltip(2) { it.addLine(I18n.format("gui.clayium.range_visualization_mode.enabled_xray")) }
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
    override fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {
        AreaMarkerRenderer.render(Cuboid6.full, rangeRelative, x, y, z, rangeRenderMode)
    }

    companion object {
        private const val INV_ROW = 3
        private const val INV_COLUMN = 3

        @JvmStatic // for protected visibility
        protected lateinit var MINER_BACK: List<BakedQuad>
    }
}