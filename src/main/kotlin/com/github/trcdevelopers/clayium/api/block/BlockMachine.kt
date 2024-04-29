package com.github.trcdevelopers.clayium.api.block

import codechicken.lib.block.property.unlisted.UnlistedTileEntityProperty
import com.cleanroommc.modularui.factory.TileEntityGuiFactory
import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState

@Suppress("OVERRIDE_DEPRECATION")
class BlockMachine : Block(Material.IRON) {

    init {
        creativeTab = Clayium.creativeTab
        soundType = SoundType.METAL
        translationKey = "clayium.machine"
        setHardness(5.0f)
        setHarvestLevel("pickaxe", 1)
        defaultState = defaultState.withProperty(IS_PIPE, false)
    }

    override fun canCreatureSpawn(state: IBlockState, world: IBlockAccess, pos: BlockPos, type: EntityLiving.SpawnPlacementType) = false

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(IS_PIPE)
            .add(TILE_ENTITY)
            .build()
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val tileEntity = world.getTileEntity(pos) as? MetaTileEntityHolder ?: return state
        return (state as IExtendedBlockState)
            .withProperty(TILE_ENTITY, tileEntity)
    }

    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(IS_PIPE, meta == 1)
    override fun getMetaFromState(state: IBlockState) = if (state.getValue(IS_PIPE)) 1 else 0

    override fun isFullBlock(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return MetaTileEntityHolder()
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val holder = worldIn.getTileEntity(pos) as? MetaTileEntityHolder ?: return
        val mte = ClayiumApi.MTE_REGISTRY.getObjectById(stack.itemDamage) ?: return
        holder.setMetaTileEntity(mte)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        CUtils.getMetaTileEntity(worldIn, pos)?.let { mte ->
            mutableListOf<ItemStack>().apply { mte.clearMachineInventory(this) }
                .forEach { spawnAsEntity(worldIn, pos, it) }

            mte.onRemoval()
        }
        super.breakBlock(worldIn, pos, state)
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        CUtils.getMetaTileEntity(world, pos)?.let { drops.add(it.getStackForm()) }
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        return if (worldIn.isRemote || worldIn.getTileEntity(pos) is MetaTileEntityHolder) {
            false
        } else {
            TileEntityGuiFactory.open(playerIn, pos)
            true
        }
    }

    override fun onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos) {
        (world.getTileEntity(pos) as? MetaTileEntityHolder)?.let {
            val facing = EnumFacing.getFacingFromVector(neighbor.x - pos.x.toFloat(), neighbor.y - pos.y.toFloat(), neighbor.z - pos.z.toFloat())
            it.onNeighborChanged(facing)
        }
    }

    companion object {
        val IS_PIPE = PropertyBool.create("is_pipe")

        val TILE_ENTITY = UnlistedTileEntityProperty("tile_entity")

        val CENTER_AABB = AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875)
        val SIDE_AABBS = listOf(
            AxisAlignedBB(0.3125, 0.0, 0.3125, 0.6875, 0.3125, 0.6875),
            AxisAlignedBB(0.3125, 0.6875, 0.3125, 0.6875, 1.0, 0.6875),
            AxisAlignedBB(0.3125, 0.3125, 0.0, 0.6875, 0.6875, 0.3125),
            AxisAlignedBB(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1.0),
            AxisAlignedBB(0.0, 0.3125, 0.3125, 0.3125, 0.6875, 0.6875),
            AxisAlignedBB(0.6875, 0.3125, 0.3125, 1.0, 0.6875, 0.6875),
        )
    }
}