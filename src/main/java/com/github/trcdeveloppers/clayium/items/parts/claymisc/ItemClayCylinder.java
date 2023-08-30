package com.github.trcdeveloppers.clayium.items.parts.claymisc;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "clay_cylinder")
public class ItemClayCylinder extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemClayCylinder() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 1;
    }
}