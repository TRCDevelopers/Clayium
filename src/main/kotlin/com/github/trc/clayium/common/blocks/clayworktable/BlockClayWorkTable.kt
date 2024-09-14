package com.github.trc.clayium.common.blocks.clayworktable

import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.common.ClayiumMod
import com.github.trc.clayium.common.GuiHandler
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler

@Suppress("OVERRIDE_DEPRECATION")
class BlockClayWorkTable : Block(Material.ROCK), ITieredBlock {
    init {
        setHardness(2.0f)
        setResistance(2.0f)
    }

    override fun getTier(stack: ItemStack) = ClayTiers.DEFAULT
    override fun getTier(world: IBlockAccess, pos: BlockPos) = ClayTiers.DEFAULT

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = TileClayWorkTable()

    override fun onBlockActivated(
        worldIn: World, pos: BlockPos, state: IBlockState,
        playerIn: EntityPlayer, hand: EnumHand,
        facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        if (worldIn.isRemote) return true
        playerIn.openGui(ClayiumMod, GuiHandler.CLAY_WORK_TABLE, worldIn, pos.x, pos.y, pos.z)
        return true
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile = worldIn.getTileEntity(pos) as TileClayWorkTable?
        if (tile != null) {
            val handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)!!
            for (i in 0..<handler.slots) {
                if (handler.getStackInSlot(i).isEmpty) continue
                spawnAsEntity(worldIn, pos, handler.getStackInSlot(i).copy())
            }
        }
        super.breakBlock(worldIn, pos, state)
    }
}
