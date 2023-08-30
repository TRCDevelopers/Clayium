package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "antimatter_seed")
public class ItemAntimatterSeed extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemAntimatterSeed() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 2;
    }
}