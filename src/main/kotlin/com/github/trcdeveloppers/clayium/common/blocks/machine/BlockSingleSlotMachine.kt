package com.github.trcdeveloppers.clayium.common.blocks.machine

import com.github.trcdeveloppers.clayium.common.blocks.UnlistedBoolean
import com.github.trcdeveloppers.clayium.common.blocks.UnlistedImportMode
import net.minecraft.block.BlockContainer
import net.minecraft.block.BlockFurnace
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
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
            .withProperty(INPUT_UP, tile.inputUp)
            .withProperty(INPUT_DOWN, tile.inputDown)
            .withProperty(INPUT_NORTH, tile.inputNorth)
            .withProperty(INPUT_SOUTH, tile.inputSouth)
            .withProperty(INPUT_EAST, tile.inputEast)
            .withProperty(INPUT_WEST, tile.inputWest)

            .withProperty(OUTPUT_UP, tile.outputUp)
            .withProperty(OUTPUT_DOWN, tile.outputDown)
            .withProperty(OUTPUT_NORTH, tile.outputNorth)
            .withProperty(OUTPUT_SOUTH, tile.outputSouth)
            .withProperty(OUTPUT_EAST, tile.outputEast)
            .withProperty(OUTPUT_WEST, tile.outputWest)
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
        worldIn.setBlockState(pos, state.withProperty(BlockFurnace.FACING, placer.horizontalFacing.opposite), Constants.BlockFlags.SEND_TO_CLIENTS)
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos, state: IBlockState,
        playerIn: EntityPlayer, hand: EnumHand,
        facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        if (!worldIn.isRemote) return true
        val tile = worldIn.getTileEntity(pos) as? TileSingleSlotMachine ?: return false

        tile.toggleInput(facing)

        worldIn.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.DEFAULT)

        return true
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    companion object {
        @JvmStatic
        val IS_PIPE: PropertyBool = PropertyBool.create("is_pipe")

        @JvmStatic
        val FACING: PropertyDirection = BlockHorizontal.FACING


        @JvmStatic
        val INPUT_UP: IUnlistedProperty<EnumImportMode> = UnlistedImportMode("input_up")
        @JvmStatic
        val INPUT_DOWN: IUnlistedProperty<EnumImportMode> = UnlistedImportMode("input_down")
        @JvmStatic
        val INPUT_NORTH: IUnlistedProperty<EnumImportMode> = UnlistedImportMode("input_north")
        @JvmStatic
        val INPUT_SOUTH: IUnlistedProperty<EnumImportMode> = UnlistedImportMode("input_south")
        @JvmStatic
        val INPUT_EAST: IUnlistedProperty<EnumImportMode> = UnlistedImportMode("input_east")
        @JvmStatic
        val INPUT_WEST: IUnlistedProperty<EnumImportMode> = UnlistedImportMode("input_west")

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

        fun getInputState(facing: EnumFacing): IUnlistedProperty<EnumImportMode> {
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