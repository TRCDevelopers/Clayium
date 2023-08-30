package com.github.trcdeveloppers.clayium.items.ingots;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.annotation.MaterialFor;
import com.github.trcdeveloppers.clayium.annotation.MaterialTypes;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@MaterialFor(materialName = "az91d", materialFor = {MaterialTypes.PLATE, MaterialTypes.LARGE_PLATE, MaterialTypes.DUST})
@CItem(registryName = "az91d_ingot")
public class ItemAz91dIngot extends Item implements ClayiumItems.ClayiumItem, IItemColor, ITiered {

    public ItemAz91dIngot() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x828C87 : tintIndex == 1 ? 0x0A280A : 0xFFFFFF;
    }

    @Override
    public int getTier() {
        return 2;
    }
}