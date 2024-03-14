package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.BlockMachineTemp.Companion.IS_PIPE
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Root of all Clayium BlockMachines.
 *
 * - can be piped (see [IS_PIPE])
 */
@Suppress("OVERRIDE_DEPRECATION")
class BlockMachineTemp(

): Block(Material.IRON) {

    init {
        setCreativeTab(Clayium.creativeTab)
        setHardness(5.0f)
        setHarvestLevel("pickaxe", 1)
        setSoundType(SoundType.METAL)
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = TODO()

    override fun canCreatureSpawn(state: IBlockState, world: IBlockAccess, pos: BlockPos, type: EntityLiving.SpawnPlacementType) = false

    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED

    override fun isFullBlock(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    companion object {
        val IS_PIPE = PropertyBool.create("is_pipe")
    }
}