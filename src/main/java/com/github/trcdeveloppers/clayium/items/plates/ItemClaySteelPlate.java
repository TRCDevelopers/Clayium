package com.github.trcdeveloppers.clayium.items.plates;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "clay_steel_plate")
public class ItemClaySteelPlate extends Item implements ClayiumItems.ClayiumItem {

    public ItemClaySteelPlate() {
        super();
        setCreativeTab(CLAYIUM);
    }
}