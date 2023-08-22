package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_tier1")
public class ItemPureAntimatterTier1 extends Item implements ClayiumItems.ClayiumItem {

    public ItemPureAntimatterTier1() {
        super();
        setCreativeTab(CLAYIUM);
    }
}