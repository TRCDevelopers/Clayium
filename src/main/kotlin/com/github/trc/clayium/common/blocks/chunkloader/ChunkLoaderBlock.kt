package com.github.trc.clayium.common.blocks.chunkloader

import com.github.trc.clayium.api.block.ITieredBlock
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.util.ToolClasses
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class ChunkLoaderBlock : Block(Material.IRON), ITieredBlock {

    init {
        soundType = SoundType.METAL
        setHardness(6.0f)
        setResistance(25f)
        setHarvestLevel(ToolClasses.PICKAXE, 0)
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = ChunkLoaderTileEntity()

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        val tileEntity = worldIn.getTileEntity(pos)
        if (tileEntity is ChunkLoaderTileEntity) {
            tileEntity.onPlace()
        }
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileEntity = worldIn.getTileEntity(pos)
        if (tileEntity is ChunkLoaderTileEntity) {
            tileEntity.onBreak()
        }
        super.breakBlock(worldIn, pos, state)
    }

    override fun getTier(stack: ItemStack): ITier = ClayTiers.PRECISION
    override fun getTier(world: IBlockAccess, pos: BlockPos): ITier = ClayTiers.PRECISION

    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED
}