package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class BlockClayLaserReflector : Block(Material.GLASS) {
    init {
        registryName = clayiumId("clay_laser_reflector")
        translationKey = registryName.toString()
        soundType = SoundType.GLASS
    }

    override fun isFullBlock(state: IBlockState) = false
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = TileEntityClayLaserReflector()
}