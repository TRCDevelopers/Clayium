package com.github.trcdeveloppers.clayium.items.parts.denseclaymisc;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "dense_clay_short_stick")
public class ItemDenseClayShortStick extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemDenseClayShortStick() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 2;
    }
}