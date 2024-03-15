package com.github.trcdevelopers.clayium.common

import com.github.trcdevelopers.clayium.client.gui.GuiClayBuffer
import com.github.trcdevelopers.clayium.client.gui.GuiClayWorkTable
import com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer.ContainerClayBuffer
import com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer.TileClayBuffer
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorktableContainer
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.TileClayWorkTable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

object GuiHandler : IGuiHandler {

    const val CLAY_WORK_TABLE = 1
    const val CLAY_BUFFER = 2

    override fun getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z)) ?: return null
        return when (id) {
            CLAY_WORK_TABLE -> ClayWorktableContainer(player.inventory, tile as TileClayWorkTable)
            CLAY_BUFFER -> ContainerClayBuffer(player.inventory, tile as TileClayBuffer)
            else -> null
        }
    }

    override fun getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z)) ?: return null
        return when (id) {
            CLAY_WORK_TABLE -> GuiClayWorkTable(player.inventory, tile as TileClayWorkTable)
            CLAY_BUFFER -> GuiClayBuffer((tile as TileClayBuffer).tier, player.inventory, tile)
            else -> null
        }
    }
}
