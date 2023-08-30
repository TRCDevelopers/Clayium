package com.github.trcdeveloppers.clayium.items.parts.largeplates;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "large_dense_clay_plate")
public class ItemLargeDenseClayPlate extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemLargeDenseClayPlate() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 2;
    }
}