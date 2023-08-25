package com.github.trcdeveloppers.clayium.items.ingots;

import com.github.trcdeveloppers.clayium.annotation.MaterialFor;
import com.github.trcdeveloppers.clayium.annotation.MaterialTypes;
import com.github.trcdeveloppers.clayium.interfaces.IColored;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@MaterialFor(materialName = "ultimate_alloy", materialFor = {MaterialTypes.PLATE, MaterialTypes.LARGE_PLATE, MaterialTypes.DUST})
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "ultimate_alloy_ingot")
public class ItemUltimateAlloyIngot extends Item implements ClayiumItems.ClayiumItem, IColored {

    public ItemUltimateAlloyIngot() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public List<String> getOreDictionaries() {
        return new ArrayList<String>() {{
            add("ingotUltimateAlloy");
        }};
    }

    @Override
    public IItemColor getColor() {
        return ((stack, tintIndex) -> tintIndex == 0 ? 0x55cd55 : (tintIndex == 1 ? 0x191919 : 0xF5A0FF));
    }
}