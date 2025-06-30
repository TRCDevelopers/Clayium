package com.github.trc.clayium.common.items.filter

import com.cleanroommc.modularui.factory.HandGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.github.trc.clayium.api.capability.IItemFilter
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

class ItemFilterDuplicator : ItemFilterBase({ IItemFilter.ALWAYS_FALSE }) {
    override fun createItemFilter(stack: ItemStack): IItemFilter {
        return IItemFilter.ALWAYS_FALSE
    }

    override fun buildUI(data: HandGuiData, syncManager: PanelSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("empty")
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        return ActionResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    override fun hasCopyFlag(stack: ItemStack): Boolean {
        return true
    }
}