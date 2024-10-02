package com.github.trc.clayium.common.util;

import com.github.trc.clayium.api.util.CUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class DebugUtils {
    @SuppressWarnings("unused")
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onTooltip(ItemTooltipEvent e) {
        if (!CUtils.isDeobfEnvironment()) return;
        if (e.getItemStack().isEmpty()) {
            return;
        }

        ItemStack stack = e.getItemStack();
        e.getToolTip().add("Metadata: " + stack.getMetadata());
        int[] oreDictIds = OreDictionary.getOreIDs(stack);
        if (oreDictIds.length == 0) return;
        e.getToolTip().add("OreDicts:");
        for (int id : oreDictIds) {
            e.getToolTip().add("  " + OreDictionary.getOreName(id));
        }
    }
}
