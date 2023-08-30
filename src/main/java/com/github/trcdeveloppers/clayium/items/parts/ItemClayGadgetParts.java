package com.github.trcdeveloppers.clayium.items.parts;

import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;
import com.github.trcdeveloppers.clayium.annotation.CItem;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "clay_gadget_parts")
public class ItemClayGadgetParts extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemClayGadgetParts() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
   public int getTier() {
       return 6;
    }
}