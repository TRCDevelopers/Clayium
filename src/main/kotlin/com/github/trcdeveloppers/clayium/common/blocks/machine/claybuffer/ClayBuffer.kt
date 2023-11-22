package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.Clayium.Companion.MOD_ID
import com.github.trcdeveloppers.clayium.common.blocks.UnlistedBoolean
import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty

// This class does not extend BlockSingleSlotMachine because it does not have CE input, or Facing.
class ClayBuffer private constructor(val tier: Int, registryName: String) : BlockContainer(Material.IRON) {

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
            .add(INPUT_DOWN).add(INPUT_UP).add(INPUT_NORTH).add(INPUT_SOUTH).add(INPUT_WEST).add(INPUT_EAST)
            .add(OUTPUT_DOWN).add(OUTPUT_UP).add(OUTPUT_NORTH).add(OUTPUT_SOUTH).add(OUTPUT_WEST).add(OUTPUT_EAST)
            .build()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return if (state.getValue(IS_PIPE)) 1 else 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(IS_PIPE, meta == 1)
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val tile = world.getTileEntity(pos) as? TileClayBuffer ?: return state

        return (state as IExtendedBlockState)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return null
    }

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    companion object {

        @JvmStatic val IS_PIPE: IProperty<Boolean> = PropertyBool.create("is_pipe")

        @JvmStatic val INPUT_DOWN: IUnlistedProperty<Boolean> = UnlistedBoolean("input_down")
        @JvmStatic val INPUT_UP: IUnlistedProperty<Boolean> = UnlistedBoolean("input_up")
        @JvmStatic val INPUT_NORTH: IUnlistedProperty<Boolean> = UnlistedBoolean("input_north")
        @JvmStatic val INPUT_SOUTH: IUnlistedProperty<Boolean> = UnlistedBoolean("input_south")
        @JvmStatic val INPUT_WEST: IUnlistedProperty<Boolean> = UnlistedBoolean("input_west")
        @JvmStatic val INPUT_EAST: IUnlistedProperty<Boolean> = UnlistedBoolean("input_east")

        @JvmStatic val OUTPUT_DOWN: IUnlistedProperty<Boolean> = UnlistedBoolean("output_down")
        @JvmStatic val OUTPUT_UP: IUnlistedProperty<Boolean> = UnlistedBoolean("output_up")
        @JvmStatic val OUTPUT_NORTH: IUnlistedProperty<Boolean> = UnlistedBoolean("output_north")
        @JvmStatic val OUTPUT_SOUTH: IUnlistedProperty<Boolean> = UnlistedBoolean("output_south")
        @JvmStatic val OUTPUT_WEST: IUnlistedProperty<Boolean> = UnlistedBoolean("output_west")
        @JvmStatic val OUTPUT_EAST: IUnlistedProperty<Boolean> = UnlistedBoolean("output_east")

        @JvmStatic val INPUTS = arrayOf(INPUT_DOWN, INPUT_UP, INPUT_NORTH, INPUT_SOUTH, INPUT_WEST, INPUT_EAST)
        @JvmStatic val OUTPUTS = arrayOf(OUTPUT_DOWN, OUTPUT_UP, OUTPUT_NORTH, OUTPUT_SOUTH, OUTPUT_WEST, OUTPUT_EAST)

        fun createBlocks(): Map<String, Block> {
            val blocks: MutableMap<String, Block> = HashMap()
            for (tier in 4..13) {
                val registryName = "clay_buffer_$tier"
                blocks[registryName] = ClayBuffer(tier, registryName)
            }
            blocks["clay_buffer_4"] = ClayBuffer(4, "clay_buffer_4")
            return blocks
        }
    }
}