package com.github.trcdeveloppers.clayium.items.parts.largeplates;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "large_adv_industrial_clay_plate")
public class ItemLargeAdvIndustrialClayPlate extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemLargeAdvIndustrialClayPlate() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 4;
    }
}