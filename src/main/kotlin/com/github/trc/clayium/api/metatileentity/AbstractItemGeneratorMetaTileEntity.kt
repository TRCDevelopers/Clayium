package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import kotlin.math.min

/**
 * A base for machines that generate items without using recipes.
 * For example, cobblestone generator and salt extractor.
 */
abstract class AbstractItemGeneratorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    validInputModes: List<MachineIoMode> = onlyNoneList,
    validOutputModes: List<MachineIoMode> = validOutputModesLists[1],
    name: String,
) : MetaTileEntity(
    metaTileEntityId, tier,
    validInputModes, validOutputModes,
    name,
) {

    open val inventoryRowSize = when (tier.numeric) {
        in 4..7 -> tier.numeric - 3
        8 -> 4
        in 9..13 -> 6
        else -> 1
    }
    open val inventoryColumnSize = when (tier.numeric) {
        in 4..7 -> tier.numeric - 2
        in 8..13 -> 9
        else -> 1
    }

    override val importItems = EmptyItemStackHandler
    override val exportItems = NotifiableItemStackHandler(this, inventoryRowSize * inventoryColumnSize, this, isExport = true)
    override val itemInventory = exportItems
    val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this, isBuffer = true)

    private var isTerrainValid = false
    private var outputFull = false
    private var progress = 0
    abstract val progressPerItem: Int
    abstract val progressPerTick: Int

    /**
     * will not be modified.
     */
    abstract val generatingItem: ItemStack

    override fun isFacingValid(facing: EnumFacing): Boolean {
        return true
    }

    override fun onPlacement() {
        setOutput(this.frontFacing, MachineIoMode.ALL)
        super.onPlacement()
    }

    override fun update() {
        super.update()
        if (isRemote) return
        if (offsetTimer % 20 == 0L) isTerrainValid = isTerrainValid()
        if (hasNotifiedOutputs) outputFull = false
        if (!isTerrainValid || outputFull) return // don't progress if terrain is invalid or output is full.

        if (canProgress()) {
            if (progressPerTick.toDouble() + progress.toDouble() > Int.MAX_VALUE.toDouble())
                progress = Int.MAX_VALUE
            else
                progress += progressPerTick
        }
        if (progress >= progressPerItem) {
            var generatingItemAmount = progress / progressPerItem
            val items = mutableListOf<ItemStack>()
            while (generatingItemAmount > 0) {
                val amount = min(generatingItemAmount, 64)
                items.add(generatingItem.copy().apply { count = amount })
                generatingItemAmount -= amount
            }
            if (!TransferUtils.insertToHandler(itemInventory, items, simulate = false)) {
                outputFull = true
            }
            progress %= progressPerItem
        }
    }

    /**
     * not called every tick.
     */
    abstract fun isTerrainValid(): Boolean

    /**
     * called every tick.
     * energy draining, etc.
     *
     * @return whether the machine can progress.
     */
    open fun canProgress(): Boolean = true

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.registerSlotGroup("machine_inventory", inventoryRowSize)
        val columnStr = "I".repeat(inventoryColumnSize)
        val matrixStr = (0..<inventoryRowSize).map { columnStr }

        return ModularPanel.defaultPanel("simple_item_generator", GUI_DEFAULT_WIDTH, 18 + inventoryRowSize * 18 + 94 + 2)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager)
                    .child(SlotGroupWidget.builder()
                        .matrix(*matrixStr.toTypedArray())
                        .key('I') { index ->
                            ItemSlot().slot(
                                SyncHandlers.itemSlot(itemInventory, index)
                                    .slotGroup("machine_inventory")
                            )
                        }.build().align(Alignment.Center))
                )
            }
    }
}