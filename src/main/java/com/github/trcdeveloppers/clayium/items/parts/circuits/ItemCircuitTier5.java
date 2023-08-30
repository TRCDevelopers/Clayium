package com.github.trcdeveloppers.clayium.items.parts.circuits;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "circuit_tier_5")
public class ItemCircuitTier5 extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemCircuitTier5() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 5;
    }
}