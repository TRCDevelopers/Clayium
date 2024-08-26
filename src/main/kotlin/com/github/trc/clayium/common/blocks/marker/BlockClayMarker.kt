package com.github.trc.clayium.common.blocks.marker

import com.github.trc.clayium.api.block.VariantBlock
import com.github.trc.clayium.common.util.ToolClasses
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class BlockClayMarker : VariantBlock<ClayMarkerType>(Material.GROUND) {
    init {
        setHardness(0.5f)
        setResistance(5.0f)
        setHarvestLevel(ToolClasses.SHOVEL, 0)
    }

    private val aabb: AxisAlignedBB = run {
        val d = 0.1875
        AxisAlignedBB(0.5 - d, 0.0, 0.5 - d, 0.5 + d, 0.5, 0.5 + d)
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return when (getEnum(state)) {
            ClayMarkerType.NO_EXTEND -> TileClayMarker.NoExtend()
            ClayMarkerType.EXTEND_TO_GROUND -> TODO()
            ClayMarkerType.EXTEND_TO_SKY -> TODO()
            ClayMarkerType.ALL_HEIGHT -> TODO()
        }
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tileEntity = worldIn.getTileEntity(pos) as? TileClayMarker ?: return false
        tileEntity.onRightClick()
        return true
    }

    override fun isFullBlock(state: IBlockState) = false
    override fun isFullCube(state: IBlockState) = false
    override fun isOpaqueCube(state: IBlockState) = false
    override fun causesSuffocation(state: IBlockState) = false

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) = aabb
}