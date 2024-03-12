package com.github.trcdevelopers.clayium.common.blocks.machine

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.unlistedproperty.UnlistedBooleanArray
import com.github.trcdevelopers.clayium.common.blocks.unlistedproperty.UnlistedMachineIo
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.EXTRACTION
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.FILTER_REMOVER
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.INSERTION
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.PIPING
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.ROTATION
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkCache
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.util.Constants

@Suppress("OVERRIDE_DEPRECATION")
class BlockMachine(
    val tier: Int,
    /**
     * receives tier and returns TileEntity
     */
    val tileEntityProvider: (Int) -> TileEntityMachine,
) : Block(Material.IRON), ItemClayConfigTool.Listener {

    init {
        setCreativeTab(Clayium.creativeTab)
        setHardness(5.0f)
        setHarvestLevel("pickaxe", 1)
        setSoundType(SoundType.METAL)

        this.defaultState = this.blockState.baseState
            .withProperty(IS_PIPE, false).withProperty(FACING, EnumFacing.NORTH).also {
                (it as IExtendedBlockState)
                    .withProperty(INPUTS, MachineIoMode.defaultStateList).withProperty(OUTPUTS, MachineIoMode.defaultStateList).withProperty(CONNECTIONS, BooleanArray(6))
            }
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = tileEntityProvider(tier)

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(IS_PIPE, FACING)
            .add(INPUTS, OUTPUTS, CONNECTIONS)
            .build()
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val isPipe = meta >= 4
        val facing = EnumFacing.byHorizontalIndex(meta % 4)
        return defaultState.withProperty(IS_PIPE, isPipe).withProperty(FACING, facing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        val offset = if (state.getValue(IS_PIPE)) 4 else 0
        return state.getValue(FACING).horizontalIndex + offset
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val ext = state as? IExtendedBlockState ?: return state
        val te = if (world is ChunkCache) world.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) else world.getTileEntity(pos)

        return if (te is TileEntityMachine) {
            ext.withProperty(INPUTS, te.inputs).withProperty(OUTPUTS, te.outputs).withProperty(CONNECTIONS, te.connections)
        } else {
            ext
        }
    }

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState {
        return defaultState.withProperty(FACING, placer.horizontalFacing.opposite)
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

    override fun onRightClicked(toolType: ItemClayConfigTool.ToolType, worldIn: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (worldIn.isRemote) return
        when (toolType) {
            PIPING -> {
                val state = worldIn.getBlockState(pos)
                if (state.block is BlockMachine) {
                    worldIn.setBlockState(pos, state.cycleProperty(IS_PIPE))
                }
            }
            ROTATION -> {
                val state = worldIn.getBlockState(pos)
                if (state.block is BlockMachine) {
                    if (state.getValue(FACING) == facing) {
                        worldIn.setBlockState(pos, state.withProperty(FACING, facing.opposite))
                    } else {
                        worldIn.setBlockState(pos, state.withProperty(FACING, facing))
                    }
                }
            }
            INSERTION, EXTRACTION, FILTER_REMOVER -> {
                /* handled by Tile Entity */
                return
            }
        }

        worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), Constants.BlockFlags.DEFAULT)
    }

    companion object {
        val IS_PIPE: PropertyBool = PropertyBool.create("is_pipe")
        val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)
        val INPUTS = UnlistedMachineIo("inputs")
        val OUTPUTS = UnlistedMachineIo("outputs")
        val CONNECTIONS = UnlistedBooleanArray("connections")
    }

}