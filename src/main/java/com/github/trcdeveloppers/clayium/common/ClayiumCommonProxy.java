package com.github.trcdeveloppers.clayium.common;

import com.github.trcdeveloppers.clayium.common.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.common.items.ClayiumItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClayiumCommonProxy {
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        ClayiumItems.registerItems(event, Side.SERVER);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ClayiumBlocks.registerBlocks(event, Side.SERVER);
    }

    public void registerItemColors() {}
}
