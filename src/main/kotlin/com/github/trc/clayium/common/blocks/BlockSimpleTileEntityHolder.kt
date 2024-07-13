package com.github.trc.clayium.common.blocks

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.factory.TileEntityGuiFactory
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BlockSimpleTileEntityHolder(
    private val tileEntityFactory: () -> TileEntity,
) : Block(Material.IRON) {
    init {
        setSoundType(SoundType.METAL)
        setHardness(2.0f)
        setResistance(5.0f)
        setHarvestLevel("pickaxe", 0)
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return tileEntityFactory()
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true

        val tileEntity = worldIn.getTileEntity(pos) ?: return true
        if (tileEntity is IGuiHolder<*>) {
            TileEntityGuiFactory.open(playerIn, pos)
        }
        return true
    }
}