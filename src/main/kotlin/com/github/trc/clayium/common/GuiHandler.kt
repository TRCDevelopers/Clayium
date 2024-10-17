package com.github.trc.clayium.common

import com.github.trc.clayium.client.gui.GuiClayWorkTable
import com.github.trc.clayium.common.blocks.clayworktable.TileClayWorkTable
import com.github.trc.clayium.common.gui.ContainerClayWorkTable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

// todo: move CWT to mUI
object GuiHandler : IGuiHandler {

    const val CLAY_WORK_TABLE = 1

    override fun getServerGuiElement(
        id: Int,
        player: EntityPlayer,
        world: World,
        x: Int,
        y: Int,
        z: Int
    ): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z)) ?: return null
        return when (id) {
            CLAY_WORK_TABLE -> ContainerClayWorkTable(player.inventory, tile as TileClayWorkTable)
            else -> null
        }
    }

    override fun getClientGuiElement(
        id: Int,
        player: EntityPlayer,
        world: World,
        x: Int,
        y: Int,
        z: Int
    ): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z)) ?: return null
        return when (id) {
            CLAY_WORK_TABLE -> GuiClayWorkTable(player.inventory, tile as TileClayWorkTable)
            else -> null
        }
    }
}
