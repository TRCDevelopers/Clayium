package com.github.trcdeveloppers.clayium.items.dusts;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "industrial_clay_dust")
public class ItemIndustrialClayDust extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemIndustrialClayDust() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 3;
    }
}