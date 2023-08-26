package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_tier2")
public class ItemPureAntimatterTier2 extends Item implements ClayiumItems.ClayiumItem, IItemColor {

    public ItemPureAntimatterTier2() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x8E1777 : (tintIndex == 1 ? 0x323200 : 0xFFFFFF);
    }
}