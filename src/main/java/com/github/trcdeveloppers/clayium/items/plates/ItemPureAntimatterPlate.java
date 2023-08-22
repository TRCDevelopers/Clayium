package com.github.trcdeveloppers.clayium.items.plates;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_plate")
public class ItemPureAntimatterPlate extends Item implements ClayiumItems.ClayiumItem {

    public ItemPureAntimatterPlate() {
        super();
        setCreativeTab(CLAYIUM);
    }
}