package com.github.trcdeveloppers.clayium.proxy;

import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClayiumClientProxy extends ClayiumCommonProxy {
    @Override
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        ClayiumItems.registerItems(event, Side.CLIENT);
    }

    @Override
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ClayiumBlocks.registerBlocks(event, Side.CLIENT);
    }

    @Override
    public void registerItemColors() {
        ClayiumItems.registerItemColors();
    }
}
