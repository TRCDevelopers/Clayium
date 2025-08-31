package io.github.trcdevelopers.clayium.common.items.filter

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.HandGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget
import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class ItemStringItemFilter(
    private val filterFactory: (String) -> IItemFilter,
    private val hintText: String? = null,
) : ItemFilterBase({ filterFactory("") }) {
    override fun createItemFilter(stack: ItemStack): IItemFilter {
        val filterString = stack.tagCompound?.getString("filterString") ?: ""
        return if (filterString.isEmpty()) {
            IItemFilter.ALWAYS_FALSE
        } else {
            filterFactory(filterString)
        }
    }

    override fun buildUI(data: HandGuiData, syncManager: PanelSyncManager): ModularPanel {
        val stack = data.usedItemStack
        val oreNameSyncValue = SyncHandlers.string(
            { stack.tagCompound?.getString("filterString") ?: "" },
            { stack.tagCompound = (stack.tagCompound ?: NBTTagCompound()).apply { setString("filterString", it) } }
        )

        MuiSlots.lockHeldItem(syncManager, data.player)
        return ModularPanel.defaultPanel("string_type_filter")
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.str(stack.displayName).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(TextFieldWidget().widthRel(0.8f)
                        .hintText(hintText)
                        .value(oreNameSyncValue)
                        .align(Alignment.Center))
                )
                .child(MuiSlots.playerInventory(0)))
    }

}