package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.block.ITieredBlock
import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.BlockQuartzCrucible.QuartzCrucibleTileEntity
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class BlockQuartzCrucible : Block(net.minecraft.block.material.Material.GLASS), ITieredBlock {
    init {
        setSoundType(SoundType.GLASS)
        setHardness(0.2f)
        setResistance(0.2f)
        setHarvestLevel("pickaxe", 0)
        defaultState = blockState.baseState.withProperty(LEVEL, 0)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, LEVEL)
    }

    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(LEVEL, meta)
    override fun getMetaFromState(state: IBlockState) = state.getValue(LEVEL)

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = QuartzCrucibleTileEntity()

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) = SELECT_AABB

    @Suppress("DEPRECATION")
    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BASE)
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_NORTH)
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_SOUTH)
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_EAST)
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_WEST)
    }

    override fun isFullBlock(state: IBlockState) = false
    override fun isFullCube(state: IBlockState) = false
    override fun isOpaqueCube(state: IBlockState) = false
    override fun causesSuffocation(state: IBlockState) = false

    override fun onEntityCollision(worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity) {
        if (worldIn.isRemote || entityIn !is EntityItem) return
        val tileEntity = worldIn.getTileEntity(pos) as? QuartzCrucibleTileEntity ?: return
        val stack = entityIn.item
        val impureSiliconIngot = OreDictUnifier.get(OrePrefix.INGOT, Material.IMPURE_SILICON)
        if (stack.isItemEqual(impureSiliconIngot)) {
            val currentLevel = state.getValue(LEVEL)
            if (currentLevel >= 9) return

            stack.shrink(1)
            if (stack.isEmpty) {
                entityIn.setDead()
            }
            tileEntity.ingotQuantity++
            worldIn.setBlockState(pos, state.withProperty(LEVEL, currentLevel + 1))
        } else if (stack.item == Items.STRING) {
            val currentLevel = state.getValue(LEVEL)
            if (currentLevel <= 0) return

            if (tileEntity.ticked >= TICKS_PER_ITEM * currentLevel) {
                stack.shrink(1)
                if (stack.isEmpty) {
                    entityIn.setDead()
                }
                tileEntity.ingotQuantity = 0
                spawnAsEntity(worldIn, pos, OreDictUnifier.get(OrePrefix.INGOT, Material.SILICON, stackSize = currentLevel))
                worldIn.setBlockState(pos, state.withProperty(LEVEL, 0))
            }
        }
    }



    override fun getTier(stack: ItemStack): ITier {
        return ClayTiers.ADVANCED
    }

    override fun getTier(world: IBlockAccess, pos: BlockPos): ITier {
        return ClayTiers.ADVANCED
    }

    // quite simple, so we can just use a nested class
    // public for registration
    class QuartzCrucibleTileEntity : TileEntity(), ITickable {
        var ingotQuantity = 0
            set(value) {
                field = value
                markDirty()
            }
        var ticked = 0
            set(value) {
                field = value
                markDirty()
            }

        override fun update() {
            if (world.isRemote
                || ingotQuantity <= 0
                || ticked >= TICKS_PER_ITEM * ingotQuantity) return
            ticked++
        }

        override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
            super.writeToNBT(compound)
            compound.setInteger("ingotQuantity", ingotQuantity)
            compound.setInteger("ticked", ticked)
            return compound
        }

        override fun readFromNBT(compound: NBTTagCompound) {
            super.readFromNBT(compound)
            ingotQuantity = compound.getInteger("ingotQuantity")
            ticked = compound.getInteger("ticked")
        }

        override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
            return oldState.block != newSate.block
        }
    }

    companion object {

        const val TICKS_PER_ITEM = 600

        val LEVEL = PropertyInteger.create("meta", 0, 9)
        val AABB_BASE = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0675, 1.0)
        val AABB_WALL_NORTH = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.75, 0.0675)
        val AABB_WALL_SOUTH = AxisAlignedBB(0.0, 0.0, 1.0 - 0.0675, 1.0, 0.75, 1.0)
        val AABB_WALL_EAST = AxisAlignedBB(1.0 - 0.0675, 0.0, 0.0, 1.0, 0.75, 1.0)
        val AABB_WALL_WEST = AxisAlignedBB(0.0, 0.0, 0.0, 0.0675, 0.75, 1.0)

        val SELECT_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0)
    }
}