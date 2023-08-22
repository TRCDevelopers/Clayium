package com.github.trcdeveloppers.clayium.items.plates;

import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "silicon_plate")
public class ItemSiliconPlate extends Item implements ClayiumItems.ClayiumItem {

    public ItemSiliconPlate() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public List<String> getOreDictionaries() {
        List<String> oreDicts = new ArrayList<>();
        oreDicts.add("itemSilicon");
        return oreDicts;
    }
}