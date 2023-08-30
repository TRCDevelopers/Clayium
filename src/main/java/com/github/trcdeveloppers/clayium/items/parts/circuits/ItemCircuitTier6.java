package com.github.trcdeveloppers.clayium.items.parts.circuits;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "circuit_tier_6")
public class ItemCircuitTier6 extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemCircuitTier6() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 7;
    }
}