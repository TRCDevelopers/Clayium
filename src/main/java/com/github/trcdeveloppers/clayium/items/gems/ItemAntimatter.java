package com.github.trcdeveloppers.clayium.items.gems;

import com.github.trcdeveloppers.clayium.interfaces.IColored;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "antimatter")
public class ItemAntimatter extends Item implements ClayiumItems.ClayiumItem, IColored {

    public ItemAntimatter() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public List<String> getOreDictionaries() {
        return new ArrayList<String>() {{
            add("gemAntimatter");
        }};
    }

    @Override
    public IItemColor getColor() {
        return ((stack, tintIndex) -> tintIndex == 0 ? 0x0000EB : tintIndex == 1 ? 0 : 0xFFFFFF);
    }
}
