package com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.Clayium.Companion.MOD_ID
import com.github.trcdevelopers.clayium.common.GuiHandler
import com.github.trcdevelopers.clayium.common.blocks.unlistedproperty.UnlistedBooleanArray
import com.github.trcdevelopers.clayium.common.interfaces.IShiftRightClickable
import com.github.trcdevelopers.clayium.common.items.ClayiumItems
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkCache
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler

class BlockClayBuffer private constructor(
    val tier: Int,
    registryName: String
) : BlockContainer(Material.IRON), IShiftRightClickable {

    init {
        this.creativeTab = Clayium.creativeTab
        this.translationKey = "$MOD_ID.$registryName"
        this.registryName = ResourceLocation(MOD_ID, registryName)
        this.blockSoundType = SoundType.METAL
        this.setHardness(5.0f)
        this.setResistance(10.0f)
        this.setHarvestLevel("pickaxe", 1)
        this.defaultState = this.blockState.baseState.withProperty(IS_PIPE, false)
    }

    override fun canEntitySpawn(state: IBlockState, entityIn: Entity): Boolean {
        return false
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(IS_PIPE)
            .add(INPUTS, OUTPUTS, CONNECTIONS)
            .build()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return if (state.getValue(IS_PIPE)) 1 else 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(IS_PIPE, meta == 1)
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val tile = if (world is ChunkCache) {
            world.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK)
        } else {
            world.getTileEntity(pos)
        } as? TileClayBuffer

        return if (tile == null) {
             (state as IExtendedBlockState)
                 .withProperty(INPUTS, BooleanArray(6))
                 .withProperty(OUTPUTS, BooleanArray(6))
                 .withProperty(CONNECTIONS, BooleanArray(6))
        } else {
             (state as IExtendedBlockState)
                 .withProperty(INPUTS, tile.inputs)
                 .withProperty(OUTPUTS, tile.outputs)
                 .withProperty(CONNECTIONS, tile.connections)
        }
    }

    override fun onBlockPlacedBy(
        worldIn: World, pos: BlockPos, state: IBlockState,
        placer: EntityLivingBase, stack: ItemStack
    ) {
        if (worldIn.isRemote) return
        val tileClayBuffer = worldIn.getTileEntity(pos) as? TileClayBuffer ?: return
        tileClayBuffer.toggleInput(EnumFacing.getDirectionFromEntityLiving(pos, placer).opposite)
        tileClayBuffer.refreshConnections()
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos, state: IBlockState,
        playerIn: EntityPlayer, hand: EnumHand,
        facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        if (hand === EnumHand.OFF_HAND) return false
        if (worldIn.isRemote) return true

        val tileClayBuffer = worldIn.getTileEntity(pos) as? TileClayBuffer ?: return false
        when (playerIn.getHeldItem(hand).item) {
            ClayiumItems.CLAY_SPATULA, ClayiumItems.CLAY_PIPING_TOOL -> worldIn.setBlockState(pos, state.withProperty(
                IS_PIPE, !state.getValue(IS_PIPE)))
            ClayiumItems.CLAY_ROLLING_PIN -> tileClayBuffer.toggleInput(facing)
            ClayiumItems.CLAY_SLICER -> tileClayBuffer.toggleOutput(facing)
            ClayiumItems.CLAY_IO_CONFIGURATOR -> tileClayBuffer.toggleInput(facing)
            else -> playerIn.openGui(Clayium.INSTANCE, GuiHandler.CLAY_BUFFER, worldIn, pos.x, pos.y, pos.z)
        }

        worldIn.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
        return true
    }

    override fun onShiftRightClicked(
        world: World, pos: BlockPos, state: IBlockState,
        player: EntityPlayer, hand: EnumHand,
        facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float,
    ): IShiftRightClickable.Result {
        if (hand === EnumHand.OFF_HAND) {
            return IShiftRightClickable.Result(
                (player.getHeldItem(EnumHand.MAIN_HAND).item === ClayiumItems.CLAY_IO_CONFIGURATOR
                    || player.getHeldItem(EnumHand.MAIN_HAND).item === ClayiumItems.CLAY_PIPING_TOOL),
                false,
            )
        }
        if (world.isRemote) {
            val isUsingTool = (player.getHeldItem(EnumHand.MAIN_HAND).item === ClayiumItems.CLAY_IO_CONFIGURATOR
                        || player.getHeldItem(EnumHand.MAIN_HAND).item === ClayiumItems.CLAY_PIPING_TOOL)
            return IShiftRightClickable.Result(isUsingTool, isUsingTool)
        }

        when (player.getHeldItem(hand).item) {
            ClayiumItems.CLAY_IO_CONFIGURATOR -> {
                (world.getTileEntity(pos) as? TileClayBuffer)?.toggleOutput(facing)
            }
            ClayiumItems.CLAY_PIPING_TOOL -> {
                (world.getTileEntity(pos) as? TileClayBuffer)?.rotate()
            }
            else -> return IShiftRightClickable.Result(false, false)
        }

        world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
        return IShiftRightClickable.Result(true, true)
    }

    override fun onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos) {
        if (world is World && !world.isRemote) {
            world.notifyBlockUpdate(neighbor, world.getBlockState(neighbor), world.getBlockState(neighbor), Constants.BlockFlags.SEND_TO_CLIENTS)
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.SEND_TO_CLIENTS)
        }
        super.onNeighborChange(world, pos, neighbor)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return TileClayBuffer(tier)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val handler = worldIn.getTileEntity(pos)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        if (handler != null) {
            for (i in 0..<handler.slots) {
                val stack = handler.getStackInSlot(i)
                if (stack.isEmpty) {
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

    override fun isFullBlock(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun isFullCube(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun isOpaqueCube(state: IBlockState) = !state.getValue(IS_PIPE)
    override fun causesSuffocation(state: IBlockState) = !state.getValue(IS_PIPE)

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        if (state.getValue(IS_PIPE)) {
            val connections = (source.getTileEntity(pos) as? TileClayBuffer)?.connections ?: return CENTER_AABB
            var aabb = CENTER_AABB
            for (i in 0..5) {
                if (connections[i]) {
                    aabb = aabb.union(SIDE_AABBS[i])
                }
            }
            return aabb
        } else {
            return FULL_BLOCK_AABB
        }
    }

    override fun addCollisionBoxToList(
        state: IBlockState, worldIn: World, pos: BlockPos,
        entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?,
        isActualState: Boolean
    ) {
        if (state.getValue(IS_PIPE)) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER_AABB)
            val connections = (worldIn.getTileEntity(pos) as? TileClayBuffer)?.connections
                ?: return super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)
            for (i in 0..5) {
                if (connections[i]) {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, SIDE_AABBS[i])
                }
            }
        } else {
            return super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState)
        }
    }

    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED
    override fun getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.addAll(UtilLocale.localizeTooltip("tooltip.clayium.buffer"))
    }

    companion object {

        val CENTER_AABB = AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875)
        val SIDE_AABBS = listOf(
            AxisAlignedBB(0.3125, 0.0, 0.3125, 0.6875, 0.3125, 0.6875),
            AxisAlignedBB(0.3125, 0.6875, 0.3125, 0.6875, 1.0, 0.6875),
            AxisAlignedBB(0.3125, 0.3125, 0.0, 0.6875, 0.6875, 0.3125),
            AxisAlignedBB(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1.0),
            AxisAlignedBB(0.0, 0.3125, 0.3125, 0.3125, 0.6875, 0.6875),
            AxisAlignedBB(0.6875, 0.3125, 0.3125, 1.0, 0.6875, 0.6875),
        )

        val IS_PIPE: IProperty<Boolean> = PropertyBool.create("is_pipe")

        val INPUTS: IUnlistedProperty<BooleanArray> = UnlistedBooleanArray("input_conditions")
        val OUTPUTS: IUnlistedProperty<BooleanArray> = UnlistedBooleanArray("output_conditions")
        val CONNECTIONS: IUnlistedProperty<BooleanArray> = UnlistedBooleanArray("connections")

        fun createBlocks(): Map<String, Block> {
            val blocks: MutableMap<String, Block> = HashMap()
            for (tier in 4..13) {
                val registryName = "clay_buffer_tier$tier"
                blocks[registryName] = BlockClayBuffer(tier, registryName)
            }
            return blocks
        }
    }
}