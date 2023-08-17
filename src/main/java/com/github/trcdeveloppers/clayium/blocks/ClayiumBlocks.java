package com.github.trcdeveloppers.clayium.blocks;


import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

public class ClayiumBlocks {
    public static void register() {
        Block b;
        ItemBlock i;
    }
    private static void register(Block b, ItemBlock i) {
        ForgeRegistries.BLOCKS.register(b);
        ForgeRegistries.ITEMS.register(i);
    }
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class BlockHolder{

    }
}
