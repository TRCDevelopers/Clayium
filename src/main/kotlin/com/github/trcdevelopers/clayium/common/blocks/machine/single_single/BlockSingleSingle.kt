package com.github.trcdevelopers.clayium.common.blocks.machine.single_single

import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class BlockSingleSingle(
    private val tier: Int,
    private val teFactory: (Int) -> TileEntity,
) : Block(Material.IRON) {

    init {
        creativeTab = Clayium.creativeTab
        blockSoundType = SoundType.METAL
        this.setHardness(5.0f)
        this.setResistance(10.0f)
        this.setHarvestLevel("pickaxe", 1)
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return teFactory(tier)
    }

    override fun canCreatureSpawn(state: IBlockState, world: IBlockAccess, pos: BlockPos, type: EntityLiving.SpawnPlacementType): Boolean {
        return false
    }

    override fun isFullBlock(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    companion object {
        private val IS_PIPE = PropertyBool.create("is_pipe")
    }
}