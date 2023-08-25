package com.github.trcdeveloppers.clayium.items.ingots;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "ultimate_alloy_ingot")
public class ItemUltimateAlloyIngot extends Item implements ClayiumItems.ClayiumItem {

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
}