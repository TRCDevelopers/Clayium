package com.github.trcdevelopers.clayium.api.block

import codechicken.lib.block.property.unlisted.UnlistedTileEntityProperty
import codechicken.lib.render.particle.CustomParticleHandler
import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.gui.MetaTileEntityGuiFactory
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.particle.ParticleManager
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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

    @Suppress("DEPRECATION")
    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        if (!state.getValue(IS_PIPE)) return super.getBoundingBox(state, source, pos)

        val connections = CUtils.getMetaTileEntity(source, pos)?.connectionsCache ?: return super.getBoundingBox(state, source, pos)
        var aabb = CENTER_AABB
        for (i in 0..5) {
            if (connections[i]) {
                aabb = aabb.union(SIDE_AABBS[i])
            }
        }
        return aabb
    }

    @Suppress("DEPRECATION")
    override fun addCollisionBoxToList(
        state: IBlockState, worldIn: World, pos: BlockPos,
        entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean
    ) {
        val metaTileEntity = CUtils.getMetaTileEntity(worldIn, pos) ?: return
        if (state.getValue(IS_PIPE)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER_AABB)
            val connections = metaTileEntity.connectionsCache
            for (i in 0..5) {
                if (connections[i]) {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, SIDE_AABBS[i])
                }
            }
        } else {
            return super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)
        }
    }

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return MetaTileEntityHolder()
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val holder = worldIn.getTileEntity(pos) as? MetaTileEntityHolder ?: return
        val sampleMetaTileEntity = ClayiumApi.MTE_REGISTRY.getObjectById(stack.itemDamage) ?: return
        val newMetaTileEntity = holder.setMetaTileEntity(sampleMetaTileEntity, placer)
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
        if (worldIn.isRemote) return true
        val tileEntity = worldIn.getTileEntity(pos)
        if (tileEntity is MetaTileEntityHolder) {
            tileEntity.metaTileEntity?.onRightClick(playerIn, hand, facing, hitX, hitY, hitZ)
            return true
        }
        return false
    }

    override fun onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos) {
        (world.getTileEntity(pos) as? MetaTileEntityHolder)?.let {
            val facing = EnumFacing.getFacingFromVector(neighbor.x - pos.x.toFloat(), neighbor.y - pos.y.toFloat(), neighbor.z - pos.z.toFloat())
            it.onNeighborChanged(facing)
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block?, fromPos: BlockPos) {
        (worldIn.getTileEntity(pos) as? MetaTileEntityHolder)?.neighborChanged()
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (mte in ClayiumApi.MTE_REGISTRY) {
            if (mte.isInCreativeTab(itemIn)) {
                items.add(mte.getStackForm())
            }
        }
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack {
        return CUtils.getMetaTileEntity(world, pos)?.getStackForm() ?: ItemStack.EMPTY
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED

    @SideOnly(Side.CLIENT)
    override fun addHitEffects(state: IBlockState, world: World, target: RayTraceResult, manager: ParticleManager): Boolean {
        CustomParticleHandler.handleHitEffects(state, world, target, manager)
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun addDestroyEffects(world: World, pos: BlockPos, manager: ParticleManager): Boolean {
        CustomParticleHandler.handleDestroyEffects(world, pos, manager)
        return true
    }

    override fun addRunningEffects(state: IBlockState, world: World, pos: BlockPos, entity: Entity): Boolean {
        if (world.isRemote) {
            CustomParticleHandler.handleRunningEffects(world, pos, state, entity)
        }
        return true
    }

    override fun addLandingEffects(state: IBlockState, worldObj: WorldServer, blockPosition: BlockPos, iblockstate: IBlockState, entity: EntityLivingBase, numberOfParticles: Int): Boolean {
        CustomParticleHandler.handleLandingEffects(worldObj, blockPosition, entity, numberOfParticles)
        return true
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