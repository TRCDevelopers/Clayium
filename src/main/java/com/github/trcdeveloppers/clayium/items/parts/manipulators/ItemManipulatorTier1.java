package com.github.trcdeveloppers.clayium.items.parts.manipulators;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "manipulator_tier1")
public class ItemManipulatorTier1 extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemManipulatorTier1() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 6;
    }
}