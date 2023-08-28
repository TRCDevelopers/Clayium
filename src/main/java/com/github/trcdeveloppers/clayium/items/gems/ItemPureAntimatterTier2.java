package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "pure_antimatter_tier2")
public class ItemPureAntimatterTier2 extends Item implements ClayiumItems.ClayiumItem, IItemColor, ITiered {

    public ItemPureAntimatterTier2() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x8E1777 : (tintIndex == 1 ? 0x323200 : 0xFFFFFF);
    }

    @Override
    public int getTier() {
        return 11;
    }
}