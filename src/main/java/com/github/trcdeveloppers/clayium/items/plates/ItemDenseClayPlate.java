package com.github.trcdeveloppers.clayium.items.plates;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@CItem(registryName = "dense_clay_plate")
public class ItemDenseClayPlate extends Item implements ITiered, ClayiumItems.ClayiumItem {

    public ItemDenseClayPlate() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 1;
    }
}
