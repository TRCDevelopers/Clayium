package com.github.trc.clayium.api.metatileentity

import codechicken.lib.vec.Cuboid6
import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.BoolValue
import com.cleanroommc.modularui.value.EnumValue
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.ToggleButton
import com.cleanroommc.modularui.widgets.layout.Grid
import com.github.trc.clayium.api.GUI_DEFAULT_HEIGHT
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.HARDNESS_UNBREAKABLE
import com.github.trc.clayium.api.LaserEnergy
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserAcceptor
import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.gui.sync.ClayLaserSyncValue
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.asWidgetResizing
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getCapability
import com.github.trc.clayium.api.util.hasCapability
import com.github.trc.clayium.api.util.toItemStack
import com.github.trc.clayium.client.model.ModelTextures
import com.github.trc.clayium.client.renderer.AreaMarkerRenderer
import com.github.trc.clayium.client.renderer.AreaMarkerRenderer.RangeRenderMode
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.util.TransferUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityBeacon
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Function
import kotlin.math.log10

abstract class AbstractMinerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    name: String,
    validInputModes: List<MachineIoMode> = validInputModesLists[0],
    validOutputModes: List<MachineIoMode> = validOutputModesLists[1],
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, name), IClayLaserAcceptor {

    override val itemInventory = ClayiumItemStackHandler(this, INV_ROW * INV_COLUMN)
    override val importItems = EmptyItemStackHandler
    override val exportItems = itemInventory
    protected val filterSlot = ClayiumItemStackHandler(this, 1)
    protected val filter: IItemFilter?
        get() = filterSlot.getStackInSlot(0).getCapability(ClayiumCapabilities.ITEM_FILTER)

    protected var progress = 0.0
    private var workingEnabled = true
    private var laser: ClayLaser? = null

    private var rangeRenderMode = RangeRenderMode.DISABLED

    protected var currentPos: BlockPos? = null

    /**
     * used for rendering.
     * null for disable range rendering.
     */
    abstract val rangeRelative: Cuboid6?

    abstract val maxBlocksPerTick: Int

    abstract fun drawEnergy(accelerationRate: Double): Boolean
    abstract fun getNextBlockPos(): BlockPos?

    /**
     * return true if the block is mined, so the next block is searched.
     * also, if all [maxBlocksPerTick] blocks are mined, [progress] will be reset.
     */
    protected open fun mine(world: World, pos: BlockPos, state: IBlockState): Boolean {
        val drops = NonNullList.create<ItemStack>()
        state.block.getDrops(drops, world, pos, state, 0)
        if (!TransferUtils.insertToHandler(itemInventory, drops, true)) return false
        TransferUtils.insertToHandler(itemInventory, drops, false)
        world.destroyBlock(pos, false)
        return true
    }

    override fun update() {
        super.update()
        if (isRemote || !workingEnabled) return
        val world = world ?: return
        val r = getAccelerationRate()
        if (!drawEnergy(r)) return
        progress += PROGRESS_PER_TICK_BASE * getAccelerationRate()

        for (i in 0..<maxBlocksPerTick) {
            val pos = this.currentPos ?: getNextBlockPos()
                ?: continue
            val state = world.getBlockState(pos)
            val filter = this.filter
            if (!(filter == null || filter.test(state.toItemStack()))) {
                this.currentPos = getNextBlockPos()
                continue
            }
            val blockHardness = state.getBlockHardness(world, pos)
            if (blockHardness == HARDNESS_UNBREAKABLE) {
                this.currentPos = getNextBlockPos()
                continue
            }

            val requiredProgress = getRequiredProgress(state, world, pos)
            if (progress < requiredProgress) return

            val mined = mine(world, pos, state)
            if (mined) {
                progress -= requiredProgress
                this.currentPos = getNextBlockPos()
            } else {
                break
            }
        }

        // all blocks are mined
        progress = 0.0
    }

    /**
     * called on the server when the reset button in the gui is pressed.
     * this is intended to reset the iteration state.
     */
    protected open fun resetButtonPressed() = true

    protected fun getAccelerationRate(): Double {
        // actual $$r = 1 + 4 * log10(energy / 1000 + 1)$$
        val energy = laser?.energy ?: return 1.0
        return 1 + 4 * log10(energy / 1000 + 1)
    }

    protected fun getRequiredProgress(state: IBlockState, world: World, pos: BlockPos): Double {
        val hardness = if (state.material.isLiquid) 1f else state.getBlockHardness(world, pos)
        return REQUIRED_PROGRESS_BASE * (0.1 + hardness)
    }

    override fun acceptLaser(irradiatedSide: EnumFacing, laser: ClayLaser?) {
        this.laser = laser
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        syncManager.registerSlotGroup("breaker_inv", INV_ROW)
        val workingEnabledSync = SyncHandlers.bool(::workingEnabled, ::workingEnabled::set)
        syncManager.syncValue("working_enabled", workingEnabledSync)
        syncManager.syncValue("clay_laser", ClayLaserSyncValue(::laser, ::laser::set))
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
            .textureGetter { IDrawable.EMPTY }
        val resetButton = ButtonWidget()
            .syncHandler(InteractionSyncHandler()
                .setOnMousePressed { if (!it.isClient) resetButtonPressed() })
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
            .child(IKey.dynamic { "Laser : ${laser?.let { LaserEnergy(it.energy).format() } ?: 0}" }.asWidgetResizing()
                .alignX(Alignment.Center.x).bottom(12)
            )
            .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(filterSlot, 0).filter { it.hasCapability(ClayiumCapabilities.ITEM_FILTER) })
                .background(ClayGuiTextures.FILTER_SLOT)
                .top(12).right(24)
                .tooltipBuilder { it.addLine(IKey.lang("gui.clayium.miner.filter")) }
            )
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("breaker", GUI_DEFAULT_WIDTH, GUI_DEFAULT_HEIGHT + 20)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun onPlacement() {
        if (this.frontFacing.axis.isHorizontal) {
            this.setOutput(this.frontFacing.rotateY(), MachineIoMode.ALL)
            this.setOutput(this.frontFacing.rotateYCCW(), MachineIoMode.ALL)
        } else {
            this.setOutput(EnumFacing.NORTH, MachineIoMode.ALL)
            this.setOutput(EnumFacing.SOUTH, MachineIoMode.ALL)
        }
        super.onPlacement()
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setBoolean("workingEnabled", workingEnabled)
        data.setTag("filterSlot", filterSlot.serializeNBT())
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        workingEnabled = data.getBoolean("workingEnabled")
        filterSlot.deserializeNBT(data.getCompoundTag("filterSlot"))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR) {
            return capability.cast(this)
        }
        return super.getCapability(capability, facing)
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