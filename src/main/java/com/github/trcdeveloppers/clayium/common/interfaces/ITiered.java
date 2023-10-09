package com.github.trcdeveloppers.clayium.common.interfaces;

import net.minecraft.item.EnumRarity;
import net.minecraftforge.common.IRarity;

public interface ITiered {
    int getTier();
    default IRarity getRarityColor() {
        switch (this.getTier()) {
            case 4:
            case 5:
            case 6:
            case 7:
                return EnumRarity.UNCOMMON;
            case 8:
            case 9:
            case 10:
            case 11:
                return EnumRarity.RARE;
            case 12:
            case 13:
            case 14:
            case 15:
                return EnumRarity.EPIC;
            default:
                return EnumRarity.COMMON;
        }
    }
}
