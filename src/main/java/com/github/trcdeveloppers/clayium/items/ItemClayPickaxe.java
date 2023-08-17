package com.github.trcdeveloppers.clayium.items;

import com.github.trcdeveloppers.clayium.annotation.Item;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@Item(registryName = "clay_pickaxe")
public class ItemClayPickaxe extends ItemPickaxe {
    public ItemClayPickaxe(){
        super(ToolMaterial.STONE);
        this.setCreativeTab(CLAYIUM);
        this.setMaxDamage(500);

    }
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items){
        if(this.isInCreativeTab(tab)){
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("item.clay_pickaxe.tooltip.line1"));
    }
}
