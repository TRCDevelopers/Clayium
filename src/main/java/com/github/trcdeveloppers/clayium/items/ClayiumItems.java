package com.github.trcdeveloppers.clayium.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

public class ClayiumItems {
    public static void register(){
        Item i;

    }
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class ItemHolder{

    }
    @SideOnly(Side.CLIENT)
    public static void registerModel(Item i, int meta, String registryName){
        net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(i,meta,new net.minecraft.client.renderer.block.model.ModelResourceLocation(MOD_ID+":"+registryName,"inventory"));
    }
}
