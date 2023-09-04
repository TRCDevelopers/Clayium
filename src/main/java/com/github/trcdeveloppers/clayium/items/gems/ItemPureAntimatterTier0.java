package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.annotation.MaterialFor;
import com.github.trcdeveloppers.clayium.annotation.CShape;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@MaterialFor(materialName = "pure_antimatter", materialFor = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
@CItem(registryName = "pure_antimatter_tier0")
public class ItemPureAntimatterTier0 extends Item implements ClayiumItems.ClayiumItem, IItemColor, ITiered {

    public ItemPureAntimatterTier0() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0xFF32FF : (tintIndex == 1 ? 0 : 0xFFFFFF);
    }

    @Override
    public int getTier() {
        return 11;
    }
}