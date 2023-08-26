package com.github.trcdeveloppers.clayium.items.ingots;

import com.github.trcdeveloppers.clayium.annotation.MaterialFor;
import com.github.trcdeveloppers.clayium.annotation.MaterialTypes;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@MaterialFor(materialName = "clayium", materialFor = {MaterialTypes.PLATE, MaterialTypes.LARGE_PLATE, MaterialTypes.DUST})
@com.github.trcdeveloppers.clayium.annotation.Item(registryName = "clayium_ingot")
public class ItemClayiumIngot extends Item implements ClayiumItems.ClayiumItem, IItemColor {

    public ItemClayiumIngot() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public List<String> getOreDictionaries() {
        return new ArrayList<String>() {{
           add("ingotClayium");
        }};
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x5AF0D2 : (tintIndex == 1 ? 0x3F4855 : 0xFFCDC8);
    }
}