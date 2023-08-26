package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_tier3")
public class ItemPureAntimatterTier3 extends Item implements ClayiumItems.ClayiumItem, IItemColor {

    public ItemPureAntimatterTier3() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x5E0D45 : (tintIndex == 1 ? 0x4B4B00 : 0xFFFFFF);
    }
}