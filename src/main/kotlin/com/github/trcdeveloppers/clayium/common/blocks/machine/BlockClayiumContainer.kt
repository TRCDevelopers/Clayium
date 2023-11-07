package com.github.trcdeveloppers.clayium.common.blocks.machine

import com.github.trcdeveloppers.clayium.common.items.ClayiumItems
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.ChunkCache
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import kotlin.Boolean

abstract class BlockClayiumContainer : BlockContainer(Material.IRON) {

    init {
        this.defaultState = this.blockState.baseState
            .withProperty(isPipe, false)

            .withProperty(INSERTION_UP, EnumInsertionModeSingle.NONE)
            .withProperty(INSERTION_DOWN, EnumInsertionModeSingle.NONE)
            .withProperty(INSERTION_NORTH, EnumInsertionModeSingle.NONE)
            .withProperty(INSERTION_SOUTH, EnumInsertionModeSingle.NONE)
            .withProperty(INSERTION_EAST, EnumInsertionModeSingle.NONE)
            .withProperty(INSERTION_WEST, EnumInsertionModeSingle.NONE)

            .withProperty(EXTRACTION_UP, false)
            .withProperty(EXTRACTION_DOWN, false)
            .withProperty(EXTRACTION_NORTH, false)
            .withProperty(EXTRACTION_SOUTH, false)
            .withProperty(EXTRACTION_EAST, false)
            .withProperty(EXTRACTION_WEST, false)
    }

    override fun onBlockActivated(
        worldIn: World, pos: BlockPos, state: IBlockState,
        playerIn: EntityPlayer, hand: EnumHand,
        facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        if (worldIn.isRemote || hand == EnumHand.OFF_HAND) {
            return true
        }

        val tile = worldIn.getTileEntity(pos) as? TileClayiumContainer ?: return true

        when (playerIn.getHeldItem(hand).item) {
            ClayiumItems.CLAY_SPATULA -> {
                // isPipe is for rendering only, so does not touched in Tile Entity.
                worldIn.setBlockState(pos, state.cycleProperty(isPipe))
            }
            ClayiumItems.CLAY_ROLLING_PIN -> {
                tile.toggleInsertion(facing)
                worldIn.setBlockState(pos, this.getActualState(state, worldIn, pos))
            }
            ClayiumItems.CLAY_SLICER -> {
                tile.toggleExtraction(facing)
                worldIn.setBlockState(pos, this.getActualState(state, worldIn, pos))
            }
            ClayiumItems.CLAY_IO_CONFIGURATOR -> {
                if (playerIn.isSneaking) {
                    tile.toggleExtraction(facing)
                } else {
                    tile.toggleInsertion(facing)
                }
                worldIn.setBlockState(pos, this.getActualState(state, worldIn, pos))
            }
            // TODO: Open GUI
            else -> playerIn.sendStatusMessage(TextComponentString("you clicked buffer."), true)
        }

        return true
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, isPipe, INSERTION_UP, INSERTION_DOWN, INSERTION_NORTH, INSERTION_SOUTH, INSERTION_EAST, INSERTION_WEST,
            EXTRACTION_UP, EXTRACTION_DOWN, EXTRACTION_NORTH, EXTRACTION_SOUTH, EXTRACTION_EAST, EXTRACTION_WEST)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return if (state.getValue(isPipe)) 1 else 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(isPipe, meta == 1)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tile = if (worldIn is ChunkCache) worldIn.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) as? TileClayiumContainer ?: return state
            else worldIn.getTileEntity(pos) as? TileClayiumContainer ?: return state

        return state .withProperty(INSERTION_UP, tile.upIn)
            .withProperty(INSERTION_DOWN, tile.downIn)
            .withProperty(INSERTION_NORTH, tile.northIn)
            .withProperty(INSERTION_SOUTH, tile.southIn)
            .withProperty(INSERTION_EAST, tile.eastIn)
            .withProperty(INSERTION_WEST, tile.westIn)
            .withProperty(EXTRACTION_UP, tile.upEx)
            .withProperty(EXTRACTION_DOWN, tile.downEx)
            .withProperty(EXTRACTION_NORTH, tile.northEx)
            .withProperty(EXTRACTION_SOUTH, tile.southEx)
            .withProperty(EXTRACTION_EAST, tile.eastEx)
            .withProperty(EXTRACTION_WEST, tile.westEx)
    }

    companion object {

        @JvmStatic
        val isPipe: IProperty<Boolean> = PropertyBool.create("is_pipe")

        @JvmStatic
        val INSERTION_UP: IProperty<EnumInsertionModeSingle> = PropertyEnum.create("insertion_up", EnumInsertionModeSingle::class.java)
        @JvmStatic
        val INSERTION_DOWN: IProperty<EnumInsertionModeSingle> = PropertyEnum.create("insertion_down", EnumInsertionModeSingle::class.java)
        @JvmStatic
        val INSERTION_NORTH: IProperty<EnumInsertionModeSingle> = PropertyEnum.create("insertion_north", EnumInsertionModeSingle::class.java)
        @JvmStatic
        val INSERTION_SOUTH: IProperty<EnumInsertionModeSingle> = PropertyEnum.create("insertion_south", EnumInsertionModeSingle::class.java)
        @JvmStatic
        val INSERTION_EAST: IProperty<EnumInsertionModeSingle> = PropertyEnum.create("insertion_east", EnumInsertionModeSingle::class.java)
        @JvmStatic
        val INSERTION_WEST: IProperty<EnumInsertionModeSingle> = PropertyEnum.create("insertion_west", EnumInsertionModeSingle::class.java)

        @JvmStatic
        val EXTRACTION_UP: IProperty<Boolean> = PropertyBool.create("extraction_up")
        @JvmStatic
        val EXTRACTION_DOWN: IProperty<Boolean> = PropertyBool.create("extraction_down")
        @JvmStatic
        val EXTRACTION_NORTH: IProperty<Boolean> = PropertyBool.create("extraction_north")
        @JvmStatic
        val EXTRACTION_SOUTH: IProperty<Boolean> = PropertyBool.create("extraction_south")
        @JvmStatic
        val EXTRACTION_EAST: IProperty<Boolean> = PropertyBool.create("extraction_east")
        @JvmStatic
        val EXTRACTION_WEST: IProperty<Boolean> = PropertyBool.create("extraction_west")

        val isPipeMapper: IStateMapper = StateMap.Builder().withName(isPipe).withSuffix("_is_pipe_clay_container").build()
    }
}