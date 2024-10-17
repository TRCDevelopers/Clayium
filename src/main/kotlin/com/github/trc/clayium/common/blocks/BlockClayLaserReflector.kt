package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IConfigurationTool
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
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

@Suppress("OVERRIDE_DEPRECATION")
class BlockClayLaserReflector : Block(Material.IRON) {
    init {
        soundType = SoundType.GLASS
        setHardness(1.0f)
        setResistance(1.0f)
        defaultState = defaultState.withProperty(FACING, EnumFacing.NORTH)
    }

    override fun canCreatureSpawn(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        type: EntityLiving.SpawnPlacementType
    ): Boolean {
        return false
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    override fun getStateFromMeta(meta: Int) =
        defaultState.withProperty(FACING, EnumFacing.byIndex(meta))

    override fun getMetaFromState(state: IBlockState) = state.getValue(FACING).index

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val stack: ItemStack = playerIn.getHeldItem(hand)
        val type =
            stack.getCapability(ClayiumCapabilities.CONFIG_TOOL, null)?.getType(playerIn.isSneaking)
                ?: return false
        if (type != IConfigurationTool.ToolType.ROTATION) return false

        val direction = state.getValue(FACING)
        if (facing == direction) {
            worldIn.setBlockState(pos, state.withProperty(FACING, facing.opposite))
        } else {
            worldIn.setBlockState(pos, state.withProperty(FACING, facing))
        }
        return true
    }

    override fun isFullBlock(state: IBlockState) = false

    override fun isFullCube(state: IBlockState) = isFullBlock(state)

    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)

    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    override fun hasTileEntity(state: IBlockState) = true

    override fun createTileEntity(world: World, state: IBlockState) = TileEntityClayLaserReflector()

    override fun getStateForPlacement(
        world: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase,
        hand: EnumHand
    ): IBlockState {
        val facing = EnumFacing.getDirectionFromEntityLiving(pos, placer)
        return defaultState.withProperty(FACING, facing)
    }

    override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT

    override fun getRenderType(state: IBlockState) = EnumBlockRenderType.INVISIBLE

    override fun getBoundingBox(
        state: IBlockState,
        source: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB {
        val direction = state.getValue(FACING)
        return when (direction) {
            EnumFacing.DOWN,
            EnumFacing.UP -> Y_AABB
            EnumFacing.NORTH,
            EnumFacing.SOUTH -> Z_AABB
            EnumFacing.WEST,
            EnumFacing.EAST -> X_AABB
        }
    }

    companion object {
        val FACING = PropertyDirection.create("direction")

        private val X_AABB =
            AxisAlignedBB(
                0.0 + 0.125,
                0.0 + 0.125 * 2.0,
                0.0 + 0.125 * 2.0,
                1.0 - 0.125,
                1.0 - 0.125 * 2.0,
                1.0 - 0.125 * 2.0
            )
        private val Y_AABB =
            AxisAlignedBB(
                0.0 + 0.125 * 2.0,
                0.0 + 0.125,
                0.0 + 0.125 * 2.0,
                1.0 - 0.125 * 2.0,
                1.0 - 0.125,
                1.0 - 0.125 * 2.0
            )
        private val Z_AABB =
            AxisAlignedBB(
                0.0 + 0.125 * 2.0,
                0.0 + 0.125 * 2.0,
                0.0 + 0.125,
                1.0 - 0.125 * 2.0,
                1.0 - 0.125 * 2.0,
                1.0 - 0.125
            )
    }
}
