package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_tier3")
public class ItemPureAntimatterTier3 extends Item implements ClayiumItems.ClayiumItem {

    public ItemPureAntimatterTier3() {
        super();
        setCreativeTab(CLAYIUM);
    }
}