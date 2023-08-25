package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.annotation.MaterialFor;
import com.github.trcdeveloppers.clayium.annotation.MaterialTypes;
import com.github.trcdeveloppers.clayium.interfaces.IColored;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@MaterialFor(materialName = "pure_antimatter", materialFor = {MaterialTypes.PLATE, MaterialTypes.LARGE_PLATE, MaterialTypes.DUST})
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "pure_antimatter_tier0")
public class ItemPureAntimatterTier0 extends Item implements ClayiumItems.ClayiumItem, IColored {

    public ItemPureAntimatterTier0() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public IItemColor getColor() {
        return ((stack, tintIndex) -> tintIndex == 0 ? 0xFF32FF : (tintIndex == 1 ? 0 : 0xFFFFFF));
    }
}