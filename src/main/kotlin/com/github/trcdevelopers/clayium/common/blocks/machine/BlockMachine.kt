package com.github.trcdevelopers.clayium.common.blocks.machine

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine.Companion.IS_PIPE
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileMachine
import com.github.trcdevelopers.clayium.common.blocks.unlistedproperty.UnlistedBooleanArray
import com.github.trcdevelopers.clayium.common.blocks.unlistedproperty.UnlistedMachineIo
import com.github.trcdevelopers.clayium.common.interfaces.ITiered
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.PIPING
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkCache
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Root of all Clayium BlockMachines.
 *
 * - can be piped (see [IS_PIPE])
 */
@Suppress("OVERRIDE_DEPRECATION")
class BlockMachine(
    override val tier: Int,
    val tileEntityProvider: (Int) -> TileMachine,
): Block(Material.IRON), ITiered, ItemClayConfigTool.Listener {

    init {
        setCreativeTab(Clayium.creativeTab)
        setHardness(5.0f)
        setHarvestLevel("pickaxe", 1)
        setSoundType(SoundType.METAL)

        defaultState = blockState.baseState
            .withProperty(IS_PIPE, false).withProperty(FACING_HORIZONTAL, EnumFacing.NORTH)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(IS_PIPE, FACING_HORIZONTAL)
            .add(INPUTS, OUTPUTS, CONNECTIONS)
            .build()
    }

    override fun getMetaFromState(state: IBlockState) = if (state.getValue(IS_PIPE)) 1 else 0
    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(IS_PIPE, meta == 1)

    override fun isFullBlock(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        if (state.getValue(IS_PIPE)) {
            val connections = (source.getTileEntity(pos) as? TileMachine)?.connections ?: return CENTER_AABB
            var aabb = CENTER_AABB
            for (i in 0..5) {
                if (connections[i]) {
                    aabb = aabb.union(SIDE_AABBS[i])
                }
            }
            return aabb
        } else {
            return FULL_BLOCK_AABB
        }
    }

    @Suppress("DEPRECATION")
    override fun addCollisionBoxToList(
        state: IBlockState, worldIn: World, pos: BlockPos,
        entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?,
        isActualState: Boolean
    ) {
        if (state.getValue(IS_PIPE)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER_AABB)
            val connections = (worldIn.getTileEntity(pos) as? TileMachine)?.connections
                ?: return super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)
            for (i in 0..5) {
                if (connections[i]) {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, SIDE_AABBS[i])
                }
            }
        } else {
            return super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)
        }
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tile = if (worldIn is ChunkCache) worldIn.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) else worldIn.getTileEntity(pos)
        return if (tile is TileMachine) {
            state.withProperty(FACING_HORIZONTAL, tile.currentFacing)
        } else {
            state
        }
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val ext = state as? IExtendedBlockState ?: return state
        val te = if (world is ChunkCache) world.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) else world.getTileEntity(pos)

        return if (te is TileMachine) {
            ext.withProperty(INPUTS, te.inputs).withProperty(OUTPUTS, te.outputs).withProperty(CONNECTIONS, te.connections)
        } else {
            ext.withProperty(INPUTS, MachineIoMode.defaultStateList).withProperty(OUTPUTS, MachineIoMode.defaultStateList).withProperty(CONNECTIONS, BooleanArray(6))
        }
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = tileEntityProvider(tier)

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true

        val tile = worldIn.getTileEntity(pos)
        return if (tile is TileMachine) {
            tile.openGui(playerIn, worldIn, pos)
            true
        } else {
            false
        }
    }

    override fun onRightClicked(toolType: ItemClayConfigTool.ToolType, world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (world.isRemote) return
        when (toolType) {
            PIPING -> {
                val state = world.getBlockState(pos)
                world.setBlockState(pos, state.cycleProperty(IS_PIPE))
            }
            else -> { /* handled by tileEntity */ }
        }
    }

    override fun canCreatureSpawn(state: IBlockState, world: IBlockAccess, pos: BlockPos, type: EntityLiving.SpawnPlacementType) = false

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(ITiered.getTierTooltip(tier))
    }

    companion object {
        val IS_PIPE = PropertyBool.create("is_pipe")
        /**
         * property name is "facing".
         */
        val FACING_HORIZONTAL = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

        val INPUTS = UnlistedMachineIo("inputs")
        val OUTPUTS = UnlistedMachineIo("outputs")
        val CONNECTIONS = UnlistedBooleanArray("connections")

        val CENTER_AABB = AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875)
        val SIDE_AABBS = listOf(
            AxisAlignedBB(0.3125, 0.0, 0.3125, 0.6875, 0.3125, 0.6875),
            AxisAlignedBB(0.3125, 0.6875, 0.3125, 0.6875, 1.0, 0.6875),
            AxisAlignedBB(0.3125, 0.3125, 0.0, 0.6875, 0.6875, 0.3125),
            AxisAlignedBB(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1.0),
            AxisAlignedBB(0.0, 0.3125, 0.3125, 0.3125, 0.6875, 0.6875),
            AxisAlignedBB(0.6875, 0.3125, 0.3125, 1.0, 0.6875, 0.6875),
        )
    }
}