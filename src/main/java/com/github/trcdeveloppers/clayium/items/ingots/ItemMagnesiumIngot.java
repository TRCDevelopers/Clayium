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
@MaterialFor(materialName = "magnesium", materialFor = MaterialTypes.DUST)
@CItem(registryName = "magnesium_ingot")
public class ItemMagnesiumIngot extends Item implements ClayiumItems.ClayiumItem, IItemColor, ITiered {

    public ItemMagnesiumIngot() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x96D296 : tintIndex == 1 ? 0x787878 : 0xFFFFFF;
    }
    
    @Override
   public int getTier() {
       return 6;
    }
}