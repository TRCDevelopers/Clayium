package com.github.trc.clayium.common.util;

import com.github.trc.clayium.api.CValues;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public class DebugUtils {
    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e) {
        if (!CValues.INSTANCE.isDeobf()) return;
        if (e.getItemStack().isEmpty()) {
            return;
        }

        int[] oreDictIds = OreDictionary.getOreIDs(e.getItemStack());
        if (oreDictIds.length == 0) return;
        e.getToolTip().add("OreDicts:");
        for (int id : oreDictIds) {
            e.getToolTip().add("  " + OreDictionary.getOreName(id));
        }
    }
}
