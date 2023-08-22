package com.github.trcdeveloppers.clayium.items.plates;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "impure_silicon_plate")
public class ItemImpureSiliconPlate extends Item implements ClayiumItems.ClayiumItem {

    public ItemImpureSiliconPlate() {
        super();
        setCreativeTab(CLAYIUM);
    }
}