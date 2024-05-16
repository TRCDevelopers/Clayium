package com.github.trcdevelopers.clayium.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class BlockClayLaserReflector : Block(Material.GLASS) {
    init {
        soundType = SoundType.GLASS
        defaultState = defaultState.withProperty(FACING, EnumFacing.NORTH)
    }

    override fun canCreatureSpawn(state: IBlockState, world: IBlockAccess, pos: BlockPos, type: EntityLiving.SpawnPlacementType): Boolean {
        return false
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(FACING, EnumFacing.byIndex(meta))
    override fun getMetaFromState(state: IBlockState) = state.getValue(FACING).index

    override fun isFullBlock(state: IBlockState) = false
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = TileEntityClayLaserReflector()

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState {
        val facing = EnumFacing.getDirectionFromEntityLiving(pos, placer)
        return defaultState.withProperty(FACING, facing)
    }

    override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT
    override fun getRenderType(state: IBlockState) = EnumBlockRenderType.INVISIBLE

    companion object {
        val FACING = PropertyDirection.create("direction")
    }
}