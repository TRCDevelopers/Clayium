package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.annotation.MaterialFor;
import com.github.trcdeveloppers.clayium.annotation.MaterialTypes;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@MaterialFor(materialName = "opa", materialFor = {MaterialTypes.PLATE, MaterialTypes.LARGE_PLATE, MaterialTypes.DUST})
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_tier8")
public class ItemPureAntimatterTier8 extends Item implements ClayiumItems.ClayiumItem, IItemColor {

    public ItemPureAntimatterTier8() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x960000 : (tintIndex == 1 ? 0xC8C800 : 0xFFFFFF);
    }
}