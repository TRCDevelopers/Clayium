package com.github.trc.clayium.common.blocks.marker

import com.github.trc.clayium.api.block.VariantBlock
import com.github.trc.clayium.common.util.ToolClasses
import com.github.trc.clayium.common.util.UtilLocale
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("OVERRIDE_DEPRECATION")
class BlockClayMarker : VariantBlock<ClayMarkerType>(Material.GROUND) {
    init {
        setHardness(0.5f)
        setResistance(5.0f)
        setHarvestLevel(ToolClasses.SHOVEL, 0)
        setSoundType(SoundType.GROUND)
    }

    override fun hasTileEntity(state: IBlockState) = true

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        // when use getEnum(state) directly in when, it will throw
        // java.lang.NoClassDefFoundError:
        // com/github/trc/clayium/common/blocks/marker/BlockClayMarker$WhenMappings
        val type: ClayMarkerType = getEnum(state)
        return when (type) {
            ClayMarkerType.NO_EXTEND -> TileClayMarker.NoExtend()
            ClayMarkerType.EXTEND_TO_GROUND -> TileClayMarker.ExtendToGround()
            ClayMarkerType.EXTEND_TO_SKY -> TileClayMarker.ExtendToSky()
            ClayMarkerType.ALL_HEIGHT -> TileClayMarker.AllHeight()
        }
    }

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
        val tileEntity = worldIn.getTileEntity(pos) as? TileClayMarker ?: return false
        tileEntity.onRightClick()
        return true
    }

    override fun isFullBlock(state: IBlockState) = false

    override fun isFullCube(state: IBlockState) = false

    override fun isOpaqueCube(state: IBlockState) = false

    override fun causesSuffocation(state: IBlockState) = false

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) =
        CLAY_MARKER_AABB

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String>,
        flagIn: ITooltipFlag
    ) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        UtilLocale.formatTooltips(tooltip, "${super.translationKey}.tooltip")
        UtilLocale.formatTooltips(
            tooltip,
            "${super.translationKey}.${getEnum(stack).lowerName}.tooltip"
        )
    }

    companion object {
        val CLAY_MARKER_AABB: AxisAlignedBB = run {
            val d = 0.1875
            AxisAlignedBB(0.5 - d, 0.5 - d, 0.5 - d, 0.5 + d, 0.5 + d, 0.5 + d)
        }
    }
}
