package com.github.trcdevelopers.clayium.common.blocks.machine

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.unlistedproperty.UnlistedMachineIo
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkCache
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.property.IExtendedBlockState

@Suppress("OVERRIDE_DEPRECATION")
class BlockMachine(
    val name: String,
    val tiers: IntArray,
    /**
     * receives tier and returns TileEntity
     */
    val tileEntityProvider: (Int) -> TileEntityMachine,
) : Block(Material.IRON) {

    init {
        setCreativeTab(Clayium.creativeTab)
        setRegistryName(Clayium.MOD_ID, name)
        setHardness(5.0f)
        setHarvestLevel("pickaxe", 1)

        this.defaultState = this.blockState.baseState.withProperty(TIER, tiers.min()).withProperty(IS_PIPE, false)
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = tileEntityProvider(state.getValue(TIER))

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(TIER, IS_PIPE)
            .add(INPUTS, OUTPUTS)
            .build()
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(TIER, meta)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(TIER)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val te = if (worldIn is ChunkCache) worldIn.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) else worldIn.getTileEntity(pos)

        return if (te is TileEntityMachine) {
            state.withProperty(IS_PIPE, te.isPipe)
        } else {
            state
        }
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val ext = state as? IExtendedBlockState ?: return state
        val te = if (world is ChunkCache) world.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) else world.getTileEntity(pos)

        return if (te is TileEntityMachine) {
            ext.withProperty(INPUTS, te.inputs).withProperty(OUTPUTS, te.outputs)
        } else {
            ext
        }
    }

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState {
        if (meta !in tiers) { return defaultState }
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand)
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

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (tier in tiers) {
            items.add(ItemStack(this, 1, tier))
        }
    }

    companion object {
        /**
         * represents the tier of this block. saved in metadata.
         *
         * [BlockMachine.createBlockState] is called before initialization of [BlockMachine]. so it must be static.
         */
        private val TIER: PropertyInteger = PropertyInteger.create("tier", 0, 13)
        /**
         * this property is not saved in metadata, but in tile entity and used in [Block.getActualState]
         */
        val IS_PIPE: PropertyBool = PropertyBool.create("is_pipe")
        val INPUTS = UnlistedMachineIo("inputs")
        val OUTPUTS = UnlistedMachineIo("outputs")
    }
}