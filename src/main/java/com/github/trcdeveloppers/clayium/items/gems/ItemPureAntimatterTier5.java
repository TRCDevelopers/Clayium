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
@CItem(registryName = "pure_antimatter_tier5")
public class ItemPureAntimatterTier5 extends Item implements ClayiumItems.ClayiumItem, IItemColor, ITiered {

    public ItemPureAntimatterTier5() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x520829 : (tintIndex == 1 ? 0x7D7D00 : 0xFFFFFF);
    }

    @Override
    public int getTier() {
        return 12;
    }
}