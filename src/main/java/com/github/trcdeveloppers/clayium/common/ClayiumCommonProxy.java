package com.github.trcdeveloppers.clayium.common;

import com.github.trcdeveloppers.clayium.common.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.common.blocks.machines.clayworktable.TileClayWorkTable;
import com.github.trcdeveloppers.clayium.common.items.ClayiumItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

public class ClayiumCommonProxy {
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        ClayiumItems.registerItems(event, Side.SERVER);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ClayiumBlocks.registerBlocks(event, Side.SERVER);
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileClayWorkTable.class, new ResourceLocation(MOD_ID, "TileClayWorkTable"));
    }

    public void registerItemColors() {}
}
