package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.pan.IPanCable
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.pan.isPanCable
import com.github.trc.clayium.common.Clayium
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("OVERRIDE_DEPRECATION")
class BlockPanCable : Block(Material.GLASS), IPanCable, ITieredBlock {
    init {
        soundType = SoundType.GLASS
        setHardness(0.2f)
        setResistance(0.2f)

        defaultState = blockState.baseState.withProperty(DOWN, false).withProperty(UP, false)
            .withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(EAST, false)
    }

    override fun createBlockState() = BlockStateContainer(this, DOWN, UP, NORTH, SOUTH, WEST, EAST)

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        var res = state
        for (side in EnumFacing.entries) {
            val property = properties[side.index]
            if (worldIn.isPanCable(pos.offset(side))) {
                res = res.withProperty(property, true)
            } else {
                res = res.withProperty(property, false)
            }
        }
        return res
    }

    override fun getMetaFromState(state: IBlockState) = 0

    override fun isFullBlock(state: IBlockState) = false
    override fun isFullCube(state: IBlockState) = false
    override fun isOpaqueCube(state: IBlockState) = false
    override fun causesSuffocation(state: IBlockState) = false

    @Suppress("DEPRECATION")
    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER_AABB)
        val s = getActualState(state, worldIn, pos)
        if (s.getValue(DOWN)) addCollisionBoxToList(pos, entityBox, collidingBoxes, DOWN_AABB)
        if (s.getValue(UP)) addCollisionBoxToList(pos, entityBox, collidingBoxes, UP_AABB)
        if (s.getValue(NORTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB)
        if (s.getValue(SOUTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB)
        if (s.getValue(WEST)) addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB)
        if (s.getValue(EAST)) addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB)
    }

    @SideOnly(Side.CLIENT)
    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        val aabb = CENTER_AABB
        val s = getActualState(state, worldIn, pos)
        if (s.getValue(DOWN)) aabb.union(DOWN_AABB)
        if (s.getValue(UP)) aabb.union(UP_AABB)
        if (s.getValue(NORTH)) aabb.union(NORTH_AABB)
        if (s.getValue(SOUTH)) aabb.union(SOUTH_AABB)
        if (s.getValue(WEST)) aabb.union(WEST_AABB)
        if (s.getValue(EAST)) aabb.union(EAST_AABB)
        return aabb.offset(pos)
    }

    override fun getTier(stack: ItemStack) = ClayTiers.PURE_ANTIMATTER
    override fun getTier(world: IBlockAccess, pos: BlockPos) = ClayTiers.PURE_ANTIMATTER

    companion object {
        const val CABLE_WIDTH = 0.125

        val DOWN = PropertyBool.create("down")
        val UP = PropertyBool.create("up")
        val NORTH = PropertyBool.create("north")
        val SOUTH = PropertyBool.create("south")
        val WEST = PropertyBool.create("west")
        val EAST = PropertyBool.create("east")

        val CENTER_AABB = AxisAlignedBB(0.5 - CABLE_WIDTH, 0.5 - CABLE_WIDTH, 0.5 - CABLE_WIDTH, 0.5 + CABLE_WIDTH, 0.5 + CABLE_WIDTH, 0.5 + CABLE_WIDTH)
        val DOWN_AABB = AxisAlignedBB(0.5 - CABLE_WIDTH, 0.0, 0.5 - CABLE_WIDTH, 0.5 + CABLE_WIDTH, 0.5 - CABLE_WIDTH, 0.5 + CABLE_WIDTH)
        val UP_AABB = AxisAlignedBB(0.5 - CABLE_WIDTH, 0.5 + CABLE_WIDTH, 0.5 - CABLE_WIDTH, 0.5 + CABLE_WIDTH, 1.0, 0.5 + CABLE_WIDTH)
        val NORTH_AABB = AxisAlignedBB(0.5 - CABLE_WIDTH, 0.5 - CABLE_WIDTH, 0.0, 0.5 + CABLE_WIDTH, 0.5 + CABLE_WIDTH, 0.5 - CABLE_WIDTH)
        val SOUTH_AABB = AxisAlignedBB(0.5 - CABLE_WIDTH, 0.5 - CABLE_WIDTH, 0.5 + CABLE_WIDTH, 0.5 + CABLE_WIDTH, 0.5 + CABLE_WIDTH, 1.0)
        val WEST_AABB = AxisAlignedBB(0.0, 0.5 - CABLE_WIDTH, 0.5 - CABLE_WIDTH, 0.5 - CABLE_WIDTH, 0.5 + CABLE_WIDTH, 0.5 + CABLE_WIDTH)
        val EAST_AABB = AxisAlignedBB(0.5 + CABLE_WIDTH, 0.5 - CABLE_WIDTH, 0.5 - CABLE_WIDTH, 1.0, 0.5 + CABLE_WIDTH, 0.5 + CABLE_WIDTH)


        private val properties = arrayOf(DOWN, UP, NORTH, SOUTH, WEST, EAST)
    }
}