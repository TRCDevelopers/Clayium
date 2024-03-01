package com.github.trcdevelopers.clayium.common.blocks.machine.single_single

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.GuiHandler
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
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
        this.defaultState = this.blockState.baseState.withProperty(IS_PIPE, false)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, IS_PIPE)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return if (state.getValue(IS_PIPE)) 1 else 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(IS_PIPE, meta == 1)
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

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) {
            return true
        }
        playerIn.openGui(Clayium.INSTANCE, GuiHandler.SINGLE_SINGLE, worldIn, pos.x, pos.y, pos.z)
        return true
    }

    override fun isFullBlock(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    companion object {
        private val IS_PIPE = PropertyBool.create("is_pipe")
    }
}