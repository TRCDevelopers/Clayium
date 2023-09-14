package com.github.trcdeveloppers.clayium.gui;

import com.github.trcdeveloppers.clayium.blocks.machines.container.ClayWorktableContainer;
import com.github.trcdeveloppers.clayium.blocks.machines.tile.TileClayWorkTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public static final int ClayWorkTable = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        System.out.println("called guihandler");
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te == null) { return null; }

        switch (ID) {
            case ClayWorkTable:
                System.out.println("server gui id 1");
                return new ClayWorktableContainer(player.inventory, (TileClayWorkTable) te);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        System.out.println("called guihandler");
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te == null) { return null; }

        switch (ID) {
            case ClayWorkTable:
                System.out.println("client gui id 1");
                return new GuiClayWorkTable(player.inventory, (TileClayWorkTable) te);
        }
        return null;
    }
}
