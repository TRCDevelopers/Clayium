package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
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
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MteRenderingConfig
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.client.renderer.AreaMarkerRenderer
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.integration.modularui.MuiSlots
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.entity.Entity
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.items.ItemHandlerHelper

class ActivatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "activator", bufferValidInputModes) {

    @Suppress("unused")
    val ioHandler = AutoIoHandler.Exporter(this)

    override val rangeRelative: Cuboid6 get() = Cuboid6.full.copy().add(this.pos?.offset(this.frontFacing.opposite) ?: BlockPos.ORIGIN)
    override val maxBlocksPerTick = 1

    private var blockEntityMode = BlockEntityMode.BLOCK
    set(value) {
        println("Block entity mode changed to $value")
        field = value
    }
    private var raytrace = false
    private var sneaking = false

    protected val scannedEntities = mutableListOf<Entity>()

    override fun drawEnergy(accelerationRate: Double): Boolean { return true }

    override fun getNextBlockPos(): BlockPos? {
        return this.pos?.offset(this.frontFacing.opposite)
    }

    override fun mine(world: World, pos: BlockPos, state: IBlockState): Boolean {
        if (this.offsetTimer % 20 != 0L) return false

        val clickPos = getNextBlockPos() ?: return false
        val world = this.world as? WorldServer ?: return false

        when (blockEntityMode) {
            BlockEntityMode.BLOCK -> this.clickBlock(world, clickPos)
            BlockEntityMode.ENTITY -> this.interactEntity(world, clickPos)
            BlockEntityMode.BLOCK_AND_ENTITY -> {}
        }

        return false
    }

    private fun clickBlock(world: World, clickPos: BlockPos): Boolean {
        val pos = this.pos ?: return false
        val world = this.world as? WorldServer ?: return false
        val player = CUtils.getFakePlayer(world)

        player.setWorld(world)
        player.setLocationAndAngles(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 0f, 0f)
        player.isSneaking = sneaking
        player.interactionManager.processRightClickBlock(
            player, world, ItemStack.EMPTY, EnumHand.MAIN_HAND, clickPos,
            this.frontFacing.opposite, 0.5f, 0.5f, 0.5f
        )
        return false
    }

    private fun interactEntity(world: World, clickPos: BlockPos): Boolean {
        if (inventoryCrowded()) return false
        val pos = this.pos ?: return false
        val world = this.world as? WorldServer ?: return false

        val heldItem: ItemStack = (0..<this.itemInventory.slots).firstNotNullOfOrNull { i ->
            val stack = this.itemInventory.getStackInSlot(i)
            if (stack.isEmpty) null else this.itemInventory.extractItem(i, Int.MAX_VALUE, false)
        } ?: ItemStack.EMPTY

        val player = CUtils.getFakePlayerWithItem(world, heldItem)

        val entities = world.getEntitiesWithinAABB(Entity::class.java, this.rangeRelative.aabb()) { !scannedEntities.contains(it) }
        if (entities.isEmpty()) {
            scannedEntities.clear()
            return false
        }
        player.interactOn(entities.first(), EnumHand.MAIN_HAND)
        toMachineInventory(player.inventory)
        return false
    }

    private fun toMachineInventory(inventoryPlayer: InventoryPlayer) {
        val world = this.world ?: return
        val pos = this.pos?.offset(this.frontFacing) ?: return
        val remains = mutableListOf<ItemStack>()
        listOf(inventoryPlayer.offHandInventory, inventoryPlayer.mainInventory, inventoryPlayer.armorInventory)
            .flatten()
            .filter { !it.isEmpty }
            .forEach {
                val remain = ItemHandlerHelper.insertItemStacked(this.itemInventory, it, false)
                if (!remain.isEmpty) remains.add(remain)
            }
        for (stack in remains) {
            Block.spawnAsEntity(world, pos, stack)
        }
    }

    private fun inventoryCrowded(): Boolean {
        return !(0..<this.itemInventory.slots).any { this.itemInventory.getStackInSlot(it).isEmpty }
    }

    override fun getRequiredProgress(state: IBlockState, world: World, pos: BlockPos): Double {
        return 0.0
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        syncManager.registerSlotGroup("breaker_inv", 3)
        val columnStr = "I".repeat(3)
        val matrixStr = (0..<3).map { columnStr }

        val startButton = ToggleButton()
            .value(SyncHandlers.bool(::workingEnabled, ::workingEnabled::set))
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
            .value(SyncHandlers.enumValue(BlockEntityMode::class.java, ::blockEntityMode, ::blockEntityMode::set))
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
            .value(SyncHandlers.bool(::raytrace, ::raytrace::set))
            .background(ClayGuiTextures.Clicker.FIXED_TARGET)
            .hoverBackground(ClayGuiTextures.Clicker.FIXED_TARGET_HOVERED)
            .selectedBackground(ClayGuiTextures.Clicker.RAYTRACE)
            .selectedHoverBackground(ClayGuiTextures.Clicker.RAYTRACE_HOVERED)
            .tooltip(false) { it.addLine(IKey.lang("gui.clayium.activator.raytrace_enabled")) }
            .tooltip(true) { it.addLine(IKey.lang("gui.clayium.activator.raytrace_disabled")) }
        val sneakingButton = ToggleButton()
            .value(SyncHandlers.bool(::sneaking, ::sneaking::set))
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

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/areaactivator"))
    }

    override fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        super.overlayQuads(quads, state, side, rand)
        if (state == null || side == null || state !is IExtendedBlockState) return
        if (side == this.frontFacing.opposite) {
            quads.add(MINER_BACK[side.index])
        }
    }

    enum class BlockEntityMode {
        BLOCK, ENTITY, BLOCK_AND_ENTITY,
    }
}