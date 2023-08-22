package com.github.trcdeveloppers.clayium.items.plates;

import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "clay_plate")
public class ItemClayPlate extends Item implements ITiered, ClayiumItems.ClayiumItem {
    public ItemClayPlate() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 1;
    }

}
