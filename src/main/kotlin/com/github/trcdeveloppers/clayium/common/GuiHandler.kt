package com.github.trcdeveloppers.clayium.common

import com.github.trcdeveloppers.clayium.client.gui.GuiClayWorkTable
import com.github.trcdeveloppers.clayium.common.blocks.machine.clayworktable.ClayWorktableContainer
import com.github.trcdeveloppers.clayium.common.blocks.machine.clayworktable.TileClayWorkTable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class GuiHandler : IGuiHandler {
    companion object {
        const val CLAY_WORK_TABLE = 1
    }

    override fun getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val te = world.getTileEntity(BlockPos(x, y, z)) ?: return null
        when (id) {
            CLAY_WORK_TABLE -> return ClayWorktableContainer(player.inventory, te as TileClayWorkTable)
        }
        return null
    }

    override fun getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val te = world.getTileEntity(BlockPos(x, y, z)) ?: return null
        when (id) {
            CLAY_WORK_TABLE -> return GuiClayWorkTable(player.inventory, te as TileClayWorkTable)
        }
        return null
    }
}
