package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.interfaces.IColored;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_tier3")
public class ItemPureAntimatterTier3 extends Item implements ClayiumItems.ClayiumItem, IColored {

    public ItemPureAntimatterTier3() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public IItemColor getColor() {
        return ((stack, tintIndex) -> tintIndex == 0 ? 0x5E0D45 : (tintIndex == 1 ? 0x4B4B00 : 0xFFFFFF));
    }
}