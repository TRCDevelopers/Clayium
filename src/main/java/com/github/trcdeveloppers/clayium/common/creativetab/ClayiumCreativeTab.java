package com.github.trcdeveloppers.clayium.common.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;
import static net.minecraft.creativetab.CreativeTabs.getNextID;

public abstract class ClayiumCreativeTab {
    public static final CreativeTabs CLAYIUM = new CreativeTabs(getNextID(), MOD_ID) {
        @SideOnly(Side.CLIENT)
        @SuppressWarnings("all")
        public ItemStack createIcon() {
            return new ItemStack(Items.CLAY_BALL);
        }
    };
}
