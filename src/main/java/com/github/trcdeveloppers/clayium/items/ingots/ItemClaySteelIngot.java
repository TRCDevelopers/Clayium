package com.github.trcdeveloppers.clayium.items.ingots;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "clay_steel_ingot")
public class ItemClaySteelIngot extends Item implements ClayiumItems.ClayiumItem {

    public ItemClaySteelIngot() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public List<String> getOreDictionaries() {
        List<String> oreDicts = new ArrayList<>();
        oreDicts.add("ingotClaySteel");
        return oreDicts;
    }
}