package com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.GuiHandler
import com.github.trcdevelopers.clayium.common.interfaces.ITiered
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler

@Suppress("OVERRIDE_DEPRECATION")
class BlockClayWorkTable : BlockContainer(Material.ROCK), ITiered {
    override val tier = 0

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return TileClayWorkTable()
    }

    override fun onBlockActivated(
        worldIn: World, pos: BlockPos, state: IBlockState,
        playerIn: EntityPlayer, hand: EnumHand,
        facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        if (worldIn.isRemote) {
            return true
        }
        val te = worldIn.getTileEntity(pos) as TileClayWorkTable? ?: return false
        playerIn.openGui(Clayium.INSTANCE, GuiHandler.CLAY_WORK_TABLE, worldIn, pos.x, pos.y, pos.z)
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(ITiered.getTierTooltip(tier))
        tooltip.add(I18n.format("tile.clayium.clay_work_table.tooltip"))
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile = worldIn.getTileEntity(pos) as TileClayWorkTable?
        if (tile != null) {
            val handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)!!
            for (i in 0 until handler.slots) {
                if (handler.getStackInSlot(i).isEmpty) {
                    continue
                }
                val f0 = worldIn.rand.nextFloat() * 0.6f + 0.1f
                val f1 = worldIn.rand.nextFloat() * 0.6f + 0.1f
                val f2 = worldIn.rand.nextFloat() * 0.6f + 0.1f
                val entityItem = EntityItem(
                    worldIn,
                    (pos.x + f0).toDouble(), (pos.y + f1).toDouble(), (pos.z + f2).toDouble(),
                    handler.getStackInSlot(i).copy()
                )
                val f3 = 0.025f
                entityItem.motionX = worldIn.rand.nextGaussian() * f3
                entityItem.motionY = worldIn.rand.nextGaussian() * f3 + 0.1f
                entityItem.motionZ = worldIn.rand.nextGaussian() * f3
                worldIn.spawnEntity(entityItem)
            }
        }
        super.breakBlock(worldIn, pos, state)
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }
}
