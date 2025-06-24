package com.github.trc.clayium.common.items.filter

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.HandGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget
import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.capability.impl.OreDictionaryItemFilter
import com.github.trc.clayium.integration.modularui.MuiSlots
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.TextFormatting

private const val ORE_NAME_NBT_KEY = "oreName"

class ItemOreDictionaryItemFilter : ItemFilterBase() {
    override fun buildUI(data: HandGuiData, syncManager: PanelSyncManager): ModularPanel {
        val stack = data.usedItemStack
        val oreNameSyncValue = SyncHandlers.string(
            { stack.tagCompound?.getString(ORE_NAME_NBT_KEY) ?: "" },
            { stack.tagCompound = (stack.tagCompound ?: NBTTagCompound()).apply { setString(ORE_NAME_NBT_KEY, it) } }
        )

        return ModularPanel.defaultPanel("simple_item_filter")
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.str(stack.displayName).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(TextFieldWidget().widthRel(0.8f)
                        .addTooltipLine("Use Regular Expression to filter items.")
                        .addTooltipLine("Example: ${TextFormatting.GRAY}'ingot.*'${TextFormatting.WHITE} will match${TextFormatting.GREEN} 'ingotIron' ${TextFormatting.RESET},${TextFormatting.GREEN} 'ingotGold'${TextFormatting.RESET}, etc.")
                        .value(oreNameSyncValue)
                        .align(Alignment.Center))
                    )
                .child(MuiSlots.playerInventory(0)))
    }

    override fun createItemFilter(stack: ItemStack): IItemFilter {
        val oreName = stack.tagCompound?.getString(ORE_NAME_NBT_KEY) ?: ""
        if (oreName.isEmpty()) return IItemFilter.ALWAYS_FALSE

        return OreDictionaryItemFilter(oreName)
    }
}