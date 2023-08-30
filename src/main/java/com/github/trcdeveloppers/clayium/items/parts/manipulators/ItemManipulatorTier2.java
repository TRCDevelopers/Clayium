package com.github.trcdeveloppers.clayium.items.parts.manipulators;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "manipulator_tier2")
public class ItemManipulatorTier2 extends Item implements ClayiumItems.ClayiumItem, ITiered {

    public ItemManipulatorTier2() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int getTier() {
        return 8;
    }
}