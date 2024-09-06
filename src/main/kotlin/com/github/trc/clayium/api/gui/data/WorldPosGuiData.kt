package com.github.trc.clayium.api.gui.data

import com.cleanroommc.modularui.factory.PosGuiData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class WorldPosGuiData(
    player: EntityPlayer,
    x: Int, y: Int, z: Int,
    // declaring as private to prevent generation of getter
    // so we can avoid a JVM name conflict with the getter in the superclass
    private val world: World,
) : PosGuiData(player, x, y, z) {

    constructor(player: EntityPlayer, pos: BlockPos, world: World) : this(player, pos.x, pos.y, pos.z, world)

    override fun getWorld(): World {
        return this.world
    }

    override fun getTileEntity(): TileEntity? {
        return this.world.getTileEntity(blockPos)
    }
}