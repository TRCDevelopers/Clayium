package io.github.trcdevelopers.clayium.common.blocks.claycraftingtable

import io.github.trcdevelopers.clayium.api.block.ITieredBlock
import io.github.trcdevelopers.clayium.api.util.ClayTiers
import io.github.trcdevelopers.clayium.api.util.toList
import io.github.trcdevelopers.clayium.common.ClayiumMod
import io.github.trcdevelopers.clayium.common.GuiHandler
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class BlockClayCraftingBoard : Block(Material.CLAY), ITieredBlock {
    init {
        setHardness(0.6f)
        setResistance(0.6f)
    }

    private val aabb = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0)
    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = TileClayCraftingBoard()
    override fun getTier(stack: ItemStack) = ClayTiers.DEFAULT
    override fun getTier(world: IBlockAccess, pos: BlockPos) = ClayTiers.DEFAULT

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!worldIn.isRemote) {
            playerIn.openGui(ClayiumMod, GuiHandler.CLAY_CRAFTING_BOARD, worldIn, pos.x, pos.y, pos.z)
        }
        return true
    }

    override fun isFullBlock(state: IBlockState) = false
    override fun isFullCube(state: IBlockState) = false
    override fun isOpaqueCube(state: IBlockState) = false
    override fun causesSuffocation(state: IBlockState) = false

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) = aabb

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileEntity = worldIn.getTileEntity(pos) as? TileClayCraftingBoard
            ?: return
        tileEntity.inventory.toList().forEach {
            if (!it.isEmpty) {
                spawnAsEntity(worldIn, pos, it)
            }
        }
    }
}