package com.github.trcdeveloppers.clayium.items.plates;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "oce_plate")
public class ItemOcePlate extends Item implements ClayiumItems.ClayiumItem {

    public ItemOcePlate() {
        super();
        setCreativeTab(CLAYIUM);
    }
}