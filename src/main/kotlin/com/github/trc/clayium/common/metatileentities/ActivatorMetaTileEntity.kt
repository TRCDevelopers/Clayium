package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.BoolValue
import com.cleanroommc.modularui.value.EnumValue
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.ToggleButton
import com.cleanroommc.modularui.widgets.layout.Grid
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.client.renderer.AreaMarkerRenderer
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.integration.modularui.MuiSlots
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer

class ActivatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "activator", bufferValidInputModes) {

    override val itemInventory = ClayiumItemStackHandler(this, 9)
    override val rangeRelative get() = Cuboid6.full.copy().add(BlockPos.ORIGIN.offset(this.frontFacing.opposite))
    override val maxBlocksPerTick = 1

    private var blockEntityMode = BlockEntityMode.BLOCK
    private var raytrace = false
    private var sneaking = false

    override fun drawEnergy(accelerationRate: Double): Boolean { return true }

    override fun getNextBlockPos(): BlockPos? {
        return this.pos?.offset(this.frontFacing.opposite)
    }

    override fun mine(world: World, pos: BlockPos, state: IBlockState): Boolean {
        val pos = this.pos ?: return false
        val clickPos = getNextBlockPos() ?: return false
        val world = this.world as? WorldServer ?: return false
        val player = CUtils.getFakePlayer(world)

        when (blockEntityMode) {
            BlockEntityMode.BLOCK -> {
                player.setWorld(world)
                player.setLocationAndAngles(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 0f, 0f)
                player.isSneaking = sneaking
                player.interactionManager.processRightClickBlock(
                    player, world, ItemStack.EMPTY, EnumHand.MAIN_HAND, clickPos,
                    this.frontFacing.opposite, 0.5f, 0.5f, 0.5f
                )
            }
            BlockEntityMode.ENTITY -> {}
            BlockEntityMode.BLOCK_AND_ENTITY -> {}
        }

        return false
    }

    override fun getRequiredProgress(state: IBlockState, world: World, pos: BlockPos): Double {
        return 0.0
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        syncManager.registerSlotGroup("breaker_inv", 3)
        val workingEnabledSync = SyncHandlers.bool(::workingEnabled, ::workingEnabled::set)
        syncManager.syncValue("working_enabled", workingEnabledSync)
        val columnStr = "I".repeat(3)
        val matrixStr = (0..<3).map { columnStr }

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
            .value(EnumValue.Dynamic(AreaMarkerRenderer.RangeRenderMode::class.java, ::rangeRenderMode, ::rangeRenderMode::set))
            .tooltip(0) { it.addLine(IKey.lang("gui.clayium.range_visualization_mode.disabled")) }
            .tooltip(1) { it.addLine(IKey.lang("gui.clayium.range_visualization_mode.enabled")) }
            .tooltip(2) { it.addLine(IKey.lang("gui.clayium.range_visualization_mode.enabled_xray")) }
        val resetButton = ButtonWidget()
            .syncHandler(InteractionSyncHandler().setOnMousePressed { if (!it.isClient) resetButtonPressed() })
            .background(ClayGuiTextures.RESET)
            .hoverBackground(ClayGuiTextures.RESET_HOVERED)

        val blockEntityButton = CycleButtonWidget()
            .length(3)
            .value(EnumValue.Dynamic(BlockEntityMode::class.java, ::blockEntityMode, ::blockEntityMode::set))
            .stateBackground(BlockEntityMode.BLOCK, ClayGuiTextures.Clicker.BLOCK)
            .stateHoverBackground(BlockEntityMode.BLOCK, ClayGuiTextures.Clicker.BLOCK_HOVERED)
            .stateBackground(BlockEntityMode.ENTITY, ClayGuiTextures.Clicker.ENTITY)
            .stateHoverBackground(BlockEntityMode.ENTITY, ClayGuiTextures.Clicker.ENTITY_HOVERED)
            .stateBackground(BlockEntityMode.BLOCK_AND_ENTITY, ClayGuiTextures.Clicker.BLOCK_AND_ENTITY)
            .stateHoverBackground(BlockEntityMode.BLOCK_AND_ENTITY, ClayGuiTextures.Clicker.BLOCK_AND_ENTITY_HOVERED)
            .tooltip(0) { it.addLine(IKey.lang("gui.clayium.activator.click_mode.block")) }
            .tooltip(1) { it.addLine(IKey.lang("gui.clayium.activator.click_mode.entity")) }
            .tooltip(2) { it.addLine(IKey.lang("gui.clayium.activator.click_mode.both")) }
        val raytraceButton = ToggleButton()
            .value(BoolValue.Dynamic(::raytrace, ::raytrace::set))
            .background(ClayGuiTextures.Clicker.FIXED_TARGET)
            .hoverBackground(ClayGuiTextures.Clicker.FIXED_TARGET_HOVERED)
            .selectedBackground(ClayGuiTextures.Clicker.RAYTRACE)
            .selectedHoverBackground(ClayGuiTextures.Clicker.RAYTRACE_HOVERED)
            .tooltip(false) { it.addLine(IKey.lang("gui.clayium.activator.raytrace_enabled")) }
            .tooltip(true) { it.addLine(IKey.lang("gui.clayium.activator.raytrace_disabled")) }
        val sneakingButton = ToggleButton()
            .value(BoolValue.Dynamic(::sneaking, ::sneaking::set))
            .background(ClayGuiTextures.Clicker.NO_SNEAK)
            .hoverBackground(ClayGuiTextures.Clicker.NO_SNEAK_HOVERED)
            .selectedBackground(ClayGuiTextures.Clicker.SNEAK)
            .selectedHoverBackground(ClayGuiTextures.Clicker.SNEAK_HOVERED)

        return super.buildMainParentWidget(syncManager)
            .child(Grid().coverChildren()
                .row(startButton, stopButton)
                .row(displayRange, resetButton)
                .minElementMargin(1, 1)
                .left(4).top(12)
            )
            .child(SlotGroupWidget.builder()
                .matrix(*matrixStr.toTypedArray())
                .key('I') { MuiSlots.itemSlotBuilder(itemInventory, it).slotGroup("breaker_inv").build() }
                .build().alignX(Alignment.TopCenter.x).top(12)
            )
            .child(Grid().coverChildren()
                .row(blockEntityButton)
                .row(raytraceButton)
                .row(sneakingButton)
                .minElementMargin(1, 1)
                .right(4).top(12)
            )
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ActivatorMetaTileEntity(metaTileEntityId, tier)
    }

    enum class BlockEntityMode {
        BLOCK, ENTITY, BLOCK_AND_ENTITY,
    }
}