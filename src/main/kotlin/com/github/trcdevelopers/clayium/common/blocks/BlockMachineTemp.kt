package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.BlockMachineTemp.Companion.IS_PIPE
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkCache
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk

/**
 * Root of all Clayium BlockMachines.
 *
 * - can be piped (see [IS_PIPE])
 */
@Suppress("OVERRIDE_DEPRECATION")
class BlockMachineTemp(
    val tier: Int,
    val tileEntityProvider: (Int) -> TileMachineTemp,
): Block(Material.IRON) {

    init {
        setCreativeTab(Clayium.creativeTab)
        setHardness(5.0f)
        setHarvestLevel("pickaxe", 1)
        setSoundType(SoundType.METAL)

        defaultState = defaultState
            .withProperty(IS_PIPE, false).withProperty(FACING_HORIZONTAL, EnumFacing.NORTH)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(IS_PIPE, FACING_HORIZONTAL)
            .build()
    }

    override fun getMetaFromState(state: IBlockState) = if (state.getValue(IS_PIPE)) 1 else 0
    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(IS_PIPE, meta == 1)

    override fun isFullBlock(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tile = if (worldIn is ChunkCache) worldIn.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) else worldIn.getTileEntity(pos)
        if (tile is TileMachineTemp) {
            return state.withProperty(FACING_HORIZONTAL, tile.currentFacing)
        } else {
            return state
        }
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = tileEntityProvider(tier)

    override fun canCreatureSpawn(state: IBlockState, world: IBlockAccess, pos: BlockPos, type: EntityLiving.SpawnPlacementType) = false

    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED

    companion object {
        val IS_PIPE = PropertyBool.create("is_pipe")
        val FACING_HORIZONTAL = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)
    }
}