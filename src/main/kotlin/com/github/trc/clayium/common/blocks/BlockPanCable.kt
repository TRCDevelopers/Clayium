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
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

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
                Clayium.LOGGER.info("Found cable at $pos, side $side")
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

        private val properties = arrayOf(DOWN, UP, NORTH, SOUTH, WEST, EAST)
    }
}