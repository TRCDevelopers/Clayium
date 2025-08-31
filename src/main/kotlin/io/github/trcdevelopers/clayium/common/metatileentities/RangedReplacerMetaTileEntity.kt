package io.github.trcdevelopers.clayium.common.metatileentities

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
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Grid
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_HEIGHT
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_WIDTH
import io.github.trcdevelopers.clayium.api.LaserEnergy
import io.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.capability.impl.EmptyItemStackHandler
import io.github.trcdevelopers.clayium.api.gui.data.MetaTileEntityGuiData
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.asWidgetResizing
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.api.util.hasCapability
import io.github.trcdevelopers.clayium.client.renderer.AreaMarkerRenderer.RangeRenderMode
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayer
import java.lang.ref.WeakReference

class RangedReplacerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : AdvancedRangedMinerMetaTileEntity(metaTileEntityId, tier, "ranged_replacer") {

    override val itemInventory = ClayiumItemStackHandler(this, 8)
    override val importItems = EmptyItemStackHandler
    override val exportItems = itemInventory
    private val replaceBlockInventory = ClayiumItemStackHandler(this, 8)

    private val fakePlayer: FakePlayer?
        get() = fakePlayerRef.get() ?: run {
            setFakePlayer()
            fakePlayerRef.get()
        }
    private lateinit var fakePlayerRef: WeakReference<FakePlayer>

    override fun onPlacement() {
        super.onPlacement()
        this.setFakePlayer()
    }

    private fun setFakePlayer() {
        val worldServer = world as? WorldServer ?: return
        fakePlayerRef = WeakReference(CUtils.getFakePlayer(worldServer))
    }

    override fun mine(state: IBlockState, world: World, pos: BlockPos): EnumActionResult {
        var itemBlockIndex = -1
        val pair = (0..<replaceBlockInventory.slots).firstNotNullOfOrNull { i ->
            val stack = replaceBlockInventory.getStackInSlot(i)
            val itemBlock = stack.takeUnless { it.isEmpty }?.item as? ItemBlock
            if (itemBlock != null) {
                itemBlockIndex = i
                Pair(stack, itemBlock)
            } else {
                null
            }
        }
        if (pair == null) {
            return EnumActionResult.FAIL
        }

        val (stack, itemBlock) = pair
        val result = super.mine(state, world, pos)
        if (result != EnumActionResult.SUCCESS) return result

        val player = fakePlayer ?: return EnumActionResult.FAIL
        val block = itemBlock.block
        val metadata = itemBlock.getMetadata(stack.metadata)
        val newState = block.getStateForPlacement(
            world, pos, EnumFacing.UP, 0.5f, 0.5f, 0.5f, metadata, player, EnumHand.MAIN_HAND
        )
        val succeed = world.setBlockState(pos, newState)
        if (succeed) {
            replaceBlockInventory.extractItem(itemBlockIndex, 1, false)
            block.onBlockPlacedBy(world, pos, newState, player, stack)
        }
        return EnumActionResult.SUCCESS
    }

    override fun createMetaTileEntity(): AdvancedRangedMinerMetaTileEntity {
        return RangedReplacerMetaTileEntity(metaTileEntityId, tier)
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: PanelSyncManager): ModularPanel {
        syncManager.registerSlotGroup("builder_inventory", 4)
        syncManager.registerSlotGroup("replace_inventory", 4)

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
        val repeatButton = ToggleButton()
            .value(SyncHandlers.bool(::repeatEnabled, ::repeatEnabled::set))
            .background(ClayGuiTextures.REPEAT)
            .hoverBackground(ClayGuiTextures.REPEAT_HOVERED)
            .selectedBackground(ClayGuiTextures.REPEAT_DISABLED)
        val displayRange = CycleButtonWidget()
            .background(ClayGuiTextures.DISPLAY_RANGE)
            .hoverBackground(ClayGuiTextures.DISPLAY_RANGE_HOVERED)
            .length(3)
            .value(EnumValue.Dynamic(RangeRenderMode::class.java, ::rangeRenderMode, ::rangeRenderMode::set))
            .tooltip(0) { it.addLine(IKey.lang("gui.clayium.range_visualization_mode.disabled")) }
            .tooltip(1) { it.addLine(IKey.lang("gui.clayium.range_visualization_mode.enabled")) }
            .tooltip(2) { it.addLine(IKey.lang("gui.clayium.range_visualization_mode.enabled_xray")) }

        return ModularPanel.defaultPanel(translationKey, GUI_DEFAULT_WIDTH, GUI_DEFAULT_HEIGHT + 40)
            .columnWithPlayerInv {
                child(
                    ParentWidget().widthRel(1f).expanded().marginBottom(2)
                        .child(IKey.str(asStackForm().displayName).asWidget()
                            .align(Alignment.TopLeft))
                        .child(IKey.lang("container.inventory").asWidget().align(Alignment.BottomLeft))
                        .child(Grid().coverChildren()
                            .row(startButton)
                            .row(stopButton)
                            .row(repeatButton)
                            .row(displayRange)
                            .minElementMargin(1)
                            .left(2).top(12))
                        .child(Column().coverChildren().top(12).alignX(Alignment.Center)
                            .child(SlotGroupWidget.builder()
                                .matrix("IIII", "IIII")
                                .key('I') { MuiSlots.itemSlotBuilder(itemInventory, it)
                                    .slotGroup("builder_inventory").takeOnly().build() }
                                .build())
                            .child(SlotGroupWidget.builder()
                                .matrix("IIII", "IIII")
                                .key('I') { MuiSlots.itemSlotBuilder(replaceBlockInventory, it).slotGroup("replace_inventory").build() }
                                .build().marginTop(2)))
                        .child(IKey.dynamic { "Laser : ${laser?.let { LaserEnergy(it.energy).format() } ?: 0}" }.asWidgetResizing()
                            .alignX(Alignment.Center.x).bottom(12))
                        .child(clayEnergyHolder.createCeTextWidget(syncManager)
                            .left(0).bottom(12))
                        .child(clayEnergyHolder.createSlotWidget()
                            .align(Alignment.BottomRight))
                        .child(MuiSlots.phantomSlotBuilder(filterSlot, 0).filter { it.hasCapability(ClayiumCapabilities.ITEM_FILTER) }.build()
                            .background(ClayGuiTextures.FILTER_SLOT)
                            .top(12).right(24)
                            .tooltipBuilder { it.addLine(IKey.lang("gui.clayium.miner.filter")) }
                        )
                        .child(MuiSlots.phantomSlotBuilder(extraFilters, 0).filter { it.hasCapability(ClayiumCapabilities.ITEM_FILTER) }.build()
                            .background(ClayGuiTextures.FILTER_SLOT)
                            .top(12 + 18 + 2).right(24)
                            .tooltipBuilder { it.addLine(IKey.lang("enchantment.lootBonusDigger")) }
                        )
                        .child(MuiSlots.phantomSlotBuilder(extraFilters, 1).filter { it.hasCapability(ClayiumCapabilities.ITEM_FILTER) }.build()
                            .background(ClayGuiTextures.FILTER_SLOT)
                            .top(12 + 18 * 2 + 2 * 2).right(24)
                            .tooltipBuilder { it.addLine(IKey.lang("enchantment.untouching")) }
                        )
                )
            }
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/ranged_replacer"))
    }
}