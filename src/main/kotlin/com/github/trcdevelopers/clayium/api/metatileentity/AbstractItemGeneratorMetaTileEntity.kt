package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.TextWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trcdevelopers.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.ItemStackHandler
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
    translationKey: String,
) : MetaTileEntity(
    metaTileEntityId, tier,
    validInputModes, validOutputModes,
    translationKey,
) {

    val inventoryRowSize = when (tier.numeric) {
        in 4..7 -> tier.numeric - 3
        8, -> 4
        in 9..13 -> 6
        else -> 1
    }
    val inventoryColumnSize = when (tier.numeric) {
        in 4..7 -> tier.numeric - 2
        in 8..13 -> 9
        else -> 1
    }

    override val itemInventory = ItemStackHandler(inventoryRowSize * inventoryColumnSize)
    override val importItems = EmptyItemStackHandler
    override val exportItems = itemInventory
    override val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this, isBuffer = true)

    private var canWork = false
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

    override fun changeIoModesOnPlacement(placer: EntityLivingBase) {
        super.changeIoModesOnPlacement(placer)
        setOutput(this.frontFacing.opposite, MachineIoMode.ALL)
    }

    override fun update() {
        super.update()
        if (world?.isRemote == true) return
        if (offsetTimer % 20 == 0L) canWork = canWork()
        if (!canWork) return

        progress += progressPerTick
        if (progress >= progressPerItem) {
            var generatingItemAmount = progress / progressPerItem
            val items = mutableListOf<ItemStack>()
            while (generatingItemAmount > 0) {
                val amount = min(generatingItemAmount, 64)
                items.add(generatingItem.copy().apply { count = amount })
                generatingItemAmount -= 64
            }
            TransferUtils.insertToHandler(itemInventory, items)
            progress %= progressPerItem
        }
    }

    abstract fun canWork(): Boolean

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.registerSlotGroup("machine_inventory", inventoryRowSize)
        val columnStr = "I".repeat(inventoryColumnSize)
        val matrixStr = (0..<inventoryRowSize).map { columnStr }

        return ModularPanel("simple_item_generator")
            .flex {
                it.size(176,  18 + inventoryRowSize * 18 + 94 + 2)
                it.align(Alignment.Center)
            }
            .child(
                TextWidget(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)))
                    .margin(6)
                    .align(Alignment.TopLeft))
            .child(Column()
                .marginTop(18)
                .child(SlotGroupWidget.builder()
                    .matrix(*matrixStr.toTypedArray())
                    .key('I') { index ->
                        ItemSlot().slot(
                            SyncHandlers.itemSlot(itemInventory, index)
                                .slotGroup("machine_inventory")
                        )
                    }
                    .build())
                .child(
                    TextWidget(IKey.lang("container.inventory"))
                        .paddingTop(1)
                        .paddingBottom(1)
                        .left(6)))
            .bindPlayerInventory()
    }
}