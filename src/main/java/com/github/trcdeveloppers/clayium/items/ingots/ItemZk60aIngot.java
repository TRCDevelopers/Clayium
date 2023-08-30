package com.github.trcdeveloppers.clayium.items.ingots;

import com.github.trcdeveloppers.clayium.annotation.MaterialFor;
import com.github.trcdeveloppers.clayium.annotation.MaterialTypes;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import com.github.trcdeveloppers.clayium.annotation.CItem;
import net.minecraft.item.ItemStack;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@MaterialFor(materialName = "zk60a", materialFor = {MaterialTypes.PLATE, MaterialTypes.LARGE_PLATE, MaterialTypes.DUST})
@CItem(registryName = "zk60a_ingot")
public class ItemZk60aIngot extends Item implements ClayiumItems.ClayiumItem, IItemColor {

    public ItemZk60aIngot() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x4B5550 : tintIndex == 1 ? 0x0A280A : 0xFFFFFF;
    }
}