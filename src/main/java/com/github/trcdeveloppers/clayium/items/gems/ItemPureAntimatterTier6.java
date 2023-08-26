package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_tier6")
public class ItemPureAntimatterTier6 extends Item implements ClayiumItems.ClayiumItem, IItemColor {

    public ItemPureAntimatterTier6() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x6E0727 : (tintIndex == 1 ? 0x969600 : 0xFFFFFF);
    }
}