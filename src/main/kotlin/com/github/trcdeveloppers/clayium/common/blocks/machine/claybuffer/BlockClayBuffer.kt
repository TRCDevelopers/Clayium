package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.Clayium.Companion.MOD_ID
import com.github.trcdeveloppers.clayium.common.GuiHandler
import com.github.trcdeveloppers.clayium.common.blocks.unlistedproperty.UnlistedBooleanArray
import com.github.trcdeveloppers.clayium.common.interfaces.IShiftRightClickable
import com.github.trcdeveloppers.clayium.common.items.ClayiumItems
import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkCache
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import net.minecraftforge.common.util.Constants

class BlockClayBuffer private constructor(
    val tier: Int,
    registryName: String
) : BlockContainer(Material.IRON), IShiftRightClickable {

    init {
        this.creativeTab = Clayium.CreativeTab
        this.translationKey = "$MOD_ID.$registryName"
        this.registryName = ResourceLocation(MOD_ID, registryName)
        this.blockSoundType = SoundType.METAL
        this.setHardness(5.0f)
        this.setResistance(10.0f)
        this.setHarvestLevel("pickaxe", 1)
        this.defaultState = this.blockState.baseState.withProperty(IS_PIPE, false)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(IS_PIPE)
            .add(INPUTS).add(OUTPUTS)
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
        } else {
             (state as IExtendedBlockState)
                 .withProperty(INPUTS, tile.inputs)
                 .withProperty(OUTPUTS, tile.outputs)
        }
    }

    override fun onBlockPlacedBy(
        worldIn: World, pos: BlockPos, state: IBlockState,
        placer: EntityLivingBase, stack: ItemStack
    ) {
        (worldIn.getTileEntity(pos) as? TileClayBuffer)?.toggleInput(
            EnumFacing.getDirectionFromEntityLiving(pos, placer).opposite
        )
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
            ClayiumItems.CLAY_SPATULA -> worldIn.setBlockState(pos, state.withProperty(IS_PIPE, !state.getValue(IS_PIPE)))
            ClayiumItems.CLAY_ROLLING_PIN -> tileClayBuffer.toggleInput(facing)
            ClayiumItems.CLAY_SLICER -> tileClayBuffer.toggleOutput(facing)
            ClayiumItems.CLAY_IO_CONFIGURATOR -> tileClayBuffer.toggleInput(facing)
            ClayiumItems.CLAY_PIPING_TOOL -> worldIn.setBlockState(pos, state.withProperty(IS_PIPE, !state.getValue(IS_PIPE)))
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
        if (world.isRemote) return IShiftRightClickable.Result(true, true)

        when (player.getHeldItem(hand).item) {
            ClayiumItems.CLAY_IO_CONFIGURATOR -> {
                (world.getTileEntity(pos) as? TileClayBuffer)?.toggleOutput(facing)
            }
            ClayiumItems.CLAY_PIPING_TOOL -> {
                //todo: rotate the block
            }
            else -> return IShiftRightClickable.Result(false, false)
        }

        world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS)
        return IShiftRightClickable.Result(true, true)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return TileClayBuffer(tier)
    }

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    companion object {

        val IS_PIPE: IProperty<Boolean> = PropertyBool.create("is_pipe")

        val INPUTS: IUnlistedProperty<BooleanArray> = UnlistedBooleanArray("input_conditions")
        val OUTPUTS: IUnlistedProperty<BooleanArray> = UnlistedBooleanArray("output_conditions")

        fun createBlocks(): Map<String, Block> {
            val blocks: MutableMap<String, Block> = HashMap()
            for (tier in 4..13) {
                val registryName = "clay_buffer_$tier"
                blocks[registryName] = BlockClayBuffer(tier, registryName)
            }
            return blocks
        }
    }
}