package io.github.trcdevelopers.clayium.common

import io.github.trcdevelopers.clayium.client.gui.GuiClayCraftingBoard
import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import io.github.trcdevelopers.clayium.common.gui.ContainerClayCraftingBoard
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

object GuiHandler : IGuiHandler {

    const val CLAY_CRAFTING_BOARD = 2

    override fun getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z)) ?: return null
        return when (id) {
            CLAY_CRAFTING_BOARD -> ContainerClayCraftingBoard(player, world, tile as TileClayCraftingBoard)
            else -> null
        }
    }

    override fun getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z)) ?: return null
        return when (id) {
            CLAY_CRAFTING_BOARD -> GuiClayCraftingBoard(player, world, tile as TileClayCraftingBoard)
            else -> null
        }
    }
}
