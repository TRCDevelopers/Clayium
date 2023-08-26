package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_tier4")
public class ItemPureAntimatterTier4 extends Item implements ClayiumItems.ClayiumItem, IItemColor {

    public ItemPureAntimatterTier4() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x32061F : (tintIndex == 1 ? 0x646400 : 0xFFFFFF);
    }
}