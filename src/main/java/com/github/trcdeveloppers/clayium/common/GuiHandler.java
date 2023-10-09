package com.github.trcdeveloppers.clayium.common;

import com.github.trcdeveloppers.clayium.common.blocks.machines.clayworktable.ClayWorktableContainer;
import com.github.trcdeveloppers.clayium.common.blocks.machines.clayworktable.TileClayWorkTable;
import com.github.trcdeveloppers.clayium.client.gui.GuiClayWorkTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public static final int CLAY_WORK_TABLE = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te == null) { return null; }

        switch (ID) {
            case CLAY_WORK_TABLE:
                return new ClayWorktableContainer(player.inventory, (TileClayWorkTable) te);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te == null) { return null; }

        switch (ID) {
            case CLAY_WORK_TABLE:
                return new GuiClayWorkTable(player.inventory, (TileClayWorkTable) te);
        }
        return null;
    }
}
