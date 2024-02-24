package com.github.trcdevelopers.clayium.common.blocks.machine.single_single

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class BlockSingleSingle : BlockContainer(Material.IRON) {

    init {
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        // tierさえあればよいだろう
        // tile(tier)
        TODO("Not yet implemented")
    }

    override fun canCreatureSpawn(state: IBlockState, world: IBlockAccess, pos: BlockPos, type: EntityLiving.SpawnPlacementType): Boolean {
        return false
    }

    companion object {

    }
}