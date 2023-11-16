package com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.Clayium.Companion.MOD_ID
import net.minecraft.block.BlockContainer
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.world.World

class ClayBuffer private constructor(val tier: Int, registryName: String) : BlockContainer(Material.IRON) {

    init {
        this.creativeTab = Clayium.CreativeTab
        this.translationKey = "$MOD_ID.$registryName"
        this.registryName = ResourceLocation(MOD_ID, registryName)
        this.blockSoundType = SoundType.METAL
        this.setHardness(5.0f)
        this.setResistance(10.0f)
        this.setHarvestLevel("pickaxe", 1)
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

//    companion object {
//
//        @JvmStatic
//        @ObjectHolder("$MOD_ID:clay_buffer_4")
//        lateinit var clayBuffer4: Block
//
//        fun createBlocks(): Map<String, Block> {
//            val blocks: MutableMap<String, Block> = HashMap()
//            for (tier in 4..13) {
//                val registryName = "clay_buffer_$tier"
//                blocks[registryName] = ClayBuffer(tier, registryName)
//            }
//            blocks["clay_buffer_4"] = ClayBuffer(4, "clay_buffer_4")
//            return blocks
//        }
//    }
}