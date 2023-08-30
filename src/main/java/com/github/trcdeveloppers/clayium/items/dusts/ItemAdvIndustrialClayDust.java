package com.github.trcdeveloppers.clayium.items.dusts;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "adv_industrial_clay_dust")
public class ItemAdvIndustrialClayDust extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemAdvIndustrialClayDust() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 4;
    }
}