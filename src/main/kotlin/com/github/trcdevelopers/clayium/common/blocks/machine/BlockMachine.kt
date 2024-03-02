package com.github.trcdevelopers.clayium.common.blocks.machine

import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class BlockMachine(
    val name: String,
    /**
     * min and max tier of the machine (inclusive)
     */
    val tierRange: IntRange,
    /**
     * receives tier and returns TileEntity
     */
    val tileEntityProvider: (Int) -> TileEntity,
) : Block(Material.IRON) {

    /**
     * represents the tier of this block. saved in metadata.
     */
    private val tierProperty: PropertyInteger = PropertyInteger.create("tier", tierRange.first, tierRange.last)

    init {
        setCreativeTab(Clayium.creativeTab)
        setRegistryName(Clayium.MOD_ID, name)
        setHardness(5.0f)
        setHarvestLevel("pickaxe", 1)

        this.defaultState = this.blockState.baseState.withProperty(tierProperty, tierRange.first).withProperty(IS_PIPE, false)
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = tileEntityProvider(state.getValue(tierProperty))

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(tierProperty, IS_PIPE)
            .build()
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(tierProperty, meta)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(tierProperty)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        // todo: is pipe property
        return state
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        //todo: inputs, outputs, connections
        return state
    }

    override fun isFullBlock(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        //todo
        return super.getBoundingBox(state, source, pos)
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        //todo
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)
    }

    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED
    override fun getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

    override fun canEntitySpawn(state: IBlockState, entityIn: Entity): Boolean {
        return false
    }

    companion object {
        /**
         * this property is not saved in metadata, but in tile entity and used in [Block.getActualState]
         */
        val IS_PIPE: PropertyBool = PropertyBool.create("is_pipe")
    }
}