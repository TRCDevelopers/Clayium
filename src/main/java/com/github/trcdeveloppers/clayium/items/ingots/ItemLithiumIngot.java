package com.github.trcdeveloppers.clayium.items.ingots;

import com.github.trcdeveloppers.clayium.annotation.MaterialFor;
import com.github.trcdeveloppers.clayium.annotation.MaterialTypes;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import com.github.trcdeveloppers.clayium.annotation.CItem;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@MaterialFor(materialName = "lithium", materialFor = MaterialTypes.DUST)
@CItem(registryName = "lithium_ingot")
public class ItemLithiumIngot extends Item implements ClayiumItems.ClayiumItem, IItemColor, ITiered {

    public ItemLithiumIngot() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public List<String> getOreDictionaries() {
        return new ArrayList<String>() {{
            add("ingotLithium");
        }};
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0xD2D296 : tintIndex == 1 ? 0x787878 : 0xFFFFFF;
    }

    @Override
    public int getTier() {
        return 6;
    }
}