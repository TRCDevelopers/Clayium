package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.Clayium.Companion.MOD_ID
import com.github.trcdeveloppers.clayium.common.blocks.machine.BlockClayiumContainer
import com.github.trcdeveloppers.clayium.common.blocks.machine.TileClayiumContainer
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.FMLCommonHandler

class ClayBuffer private constructor(val tier: Int, registryName: String) : BlockClayiumContainer() {

    init {
        this.creativeTab = Clayium.CreativeTab
        this.translationKey = "$MOD_ID.$registryName"
        this.registryName = ResourceLocation(MOD_ID, registryName)
        this.blockSoundType = SoundType.METAL
        this.setHardness(5.0f)
        this.setResistance(10.0f)
        this.setHarvestLevel("pickaxe", 1)
        if (FMLCommonHandler.instance().side.isClient) {
            ModelLoader.setCustomStateMapper(this, isPipeMapper)
        }
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return TileClayiumContainer(this)
    }

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    companion object {
        fun createBlocks(): Map<String, Block> {
            val blocks: MutableMap<String, Block> = HashMap()
//            for (tier in 4..13) {
//                val registryName = "clay_buffer_$tier"
//                blocks[registryName] = ClayBuffer(tier, registryName)
//            }
            blocks["clay_buffer_4"] = ClayBuffer(4, "clay_buffer_4")
            return blocks
        }
    }
}