package com.github.trcdeveloppers.clayium.common.blocks.machine

import com.github.trcdeveloppers.clayium.common.blocks.unlistedproperty.UnlistedBoolean
import com.github.trcdeveloppers.clayium.common.blocks.unlistedproperty.UnlistedImportMode
import net.minecraft.block.BlockContainer
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import net.minecraftforge.common.util.Constants

abstract class BlockSingleSlotMachine : BlockContainer(Material.IRON) {

    init {
        this.blockSoundType = SoundType.METAL
        this.defaultState = this.blockState.baseState
            .withProperty(IS_PIPE, false)
            .withProperty(FACING, EnumFacing.NORTH)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(IS_PIPE).add(FACING)
            .add(INPUT_UP).add(INPUT_DOWN).add(INPUT_NORTH).add(INPUT_SOUTH).add(INPUT_EAST).add(INPUT_WEST)
            .add(OUTPUT_UP).add(OUTPUT_DOWN).add(OUTPUT_NORTH).add(OUTPUT_SOUTH).add(OUTPUT_EAST).add(OUTPUT_WEST)
            .build()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return if (state.getValue(IS_PIPE)) {
            state.getValue(FACING).horizontalIndex
        } else {
            state.getValue(FACING).horizontalIndex + 4
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return if (meta < 4) {
            this.defaultState
                .withProperty(IS_PIPE, true)
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta))
        } else {
            this.defaultState
                .withProperty(IS_PIPE, false)
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta - 4))
        }
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val tile = world.getTileEntity(pos) as? TileSingleSlotMachine ?: return state

        return (state as IExtendedBlockState)
            .withProperty(INPUT_UP, tile.getInput(EnumFacing.UP))
            .withProperty(INPUT_DOWN, tile.getInput(EnumFacing.DOWN))
            .withProperty(INPUT_NORTH, tile.getInput(EnumFacing.NORTH))
            .withProperty(INPUT_SOUTH, tile.getInput(EnumFacing.SOUTH))
            .withProperty(INPUT_EAST, tile.getInput(EnumFacing.EAST))
            .withProperty(INPUT_WEST, tile.getInput(EnumFacing.WEST))

            .withProperty(OUTPUT_UP, tile.getOutput(EnumFacing.UP))
            .withProperty(OUTPUT_DOWN, tile.getOutput(EnumFacing.DOWN))
            .withProperty(OUTPUT_NORTH, tile.getOutput(EnumFacing.NORTH))
            .withProperty(OUTPUT_SOUTH, tile.getOutput(EnumFacing.SOUTH))
            .withProperty(OUTPUT_EAST, tile.getOutput(EnumFacing.EAST))
            .withProperty(OUTPUT_WEST, tile.getOutput(EnumFacing.WEST))
    }

    override fun getStateForPlacement(
        world: World, pos: BlockPos, facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float,
        meta: Int,
        placer: EntityLivingBase, hand: EnumHand
    ): IBlockState {
        return this.defaultState.withProperty(FACING, placer.horizontalFacing.opposite)
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.horizontalFacing.opposite))
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos, state: IBlockState,
        playerIn: EntityPlayer, hand: EnumHand,
        facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        if (hand == EnumHand.OFF_HAND) return false
        if (worldIn.isRemote) return true

        val tile = worldIn.getTileEntity(pos) as? TileSingleSlotMachine ?: return false

        when (playerIn.getHeldItem(hand).item.registryName?.path) {
            "clay_spatula" -> worldIn.setBlockState(pos, state.withProperty(IS_PIPE, !state.getValue(IS_PIPE)))
            else -> {
                tile.toggleInput(facing)
                worldIn.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.DEFAULT)
            }
        }
        return true
    }

    override fun isFullBlock(state: IBlockState): Boolean {
        return !state.getValue(IS_PIPE)
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return !state.getValue(IS_PIPE)
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return !state.getValue(IS_PIPE)
    }

    override fun addCollisionBoxToList(
        state: IBlockState,
        worldIn: World,
        pos: BlockPos,
        entityBox: AxisAlignedBB,
        collidingBoxes: MutableList<AxisAlignedBB>,
        entityIn: Entity?,
        isActualState: Boolean
    ) {
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return if (state.getValue(IS_PIPE)) {
            PIPE_CENTER_AABB
        } else {
            FULL_BLOCK_AABB
        }
    }

    protected fun canConnectTo(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val tile = world.getTileEntity(pos) as? TileSingleSlotMachine ?: return false
        //todo:
        return false
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    companion object {

        val PIPE_CENTER_AABB = AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75)

        @JvmStatic
        val IS_PIPE: PropertyBool = PropertyBool.create("is_pipe")

        @JvmStatic
        val FACING: PropertyDirection = BlockHorizontal.FACING

        @JvmStatic
        val INPUT_UP: IUnlistedProperty<EnumIoMode> = UnlistedImportMode("input_up")
        @JvmStatic
        val INPUT_DOWN: IUnlistedProperty<EnumIoMode> = UnlistedImportMode("input_down")
        @JvmStatic
        val INPUT_NORTH: IUnlistedProperty<EnumIoMode> = UnlistedImportMode("input_north")
        @JvmStatic
        val INPUT_SOUTH: IUnlistedProperty<EnumIoMode> = UnlistedImportMode("input_south")
        @JvmStatic
        val INPUT_EAST: IUnlistedProperty<EnumIoMode> = UnlistedImportMode("input_east")
        @JvmStatic
        val INPUT_WEST: IUnlistedProperty<EnumIoMode> = UnlistedImportMode("input_west")

        @JvmStatic
        val OUTPUT_UP: IUnlistedProperty<Boolean> = UnlistedBoolean("output_up")
        @JvmStatic
        val OUTPUT_DOWN: IUnlistedProperty<Boolean> = UnlistedBoolean("output_down")
        @JvmStatic
        val OUTPUT_NORTH: IUnlistedProperty<Boolean> = UnlistedBoolean("output_north")
        @JvmStatic
        val OUTPUT_SOUTH: IUnlistedProperty<Boolean> = UnlistedBoolean("output_south")
        @JvmStatic
        val OUTPUT_EAST: IUnlistedProperty<Boolean> = UnlistedBoolean("output_east")
        @JvmStatic
        val OUTPUT_WEST: IUnlistedProperty<Boolean> = UnlistedBoolean("output_west")

        fun getInputState(facing: EnumFacing): IUnlistedProperty<EnumIoMode> {
            return when (facing) {
                EnumFacing.UP -> INPUT_UP
                EnumFacing.DOWN -> INPUT_DOWN
                EnumFacing.NORTH -> INPUT_NORTH
                EnumFacing.SOUTH -> INPUT_SOUTH
                EnumFacing.EAST -> INPUT_EAST
                EnumFacing.WEST -> INPUT_WEST
            }
        }

        fun getOutputState(facing: EnumFacing): IUnlistedProperty<Boolean> {
            return when (facing) {
                EnumFacing.UP -> OUTPUT_UP
                EnumFacing.DOWN -> OUTPUT_DOWN
                EnumFacing.NORTH -> OUTPUT_NORTH
                EnumFacing.SOUTH -> OUTPUT_SOUTH
                EnumFacing.EAST -> OUTPUT_EAST
                EnumFacing.WEST -> OUTPUT_WEST
            }
        }
    }
}