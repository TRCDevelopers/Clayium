package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.clay.BlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.clay.BlockEnergizedClay
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.BlockClayWorkTable
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockClayOre
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockDenseClayOre
import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * holds non-functional blocks and non-BlockMachine machines (e.g. ClayWorkTable, ClayBuffer)
 * for functional blocks, see [MachineBlocks]
 */
object ClayiumBlocks {

    val CLAY_WORK_TABLE = createBlock("clay_work_table", BlockClayWorkTable())

    val COMPRESSED_CLAY = createBlock("compressed_clay", BlockCompressedClay())
    val ENERGIZED_CLAY = createBlock("energized_clay", BlockEnergizedClay())

    val CLAY_ORE = createBlock("clay_ore", BlockClayOre())
    val DENSE_CLAY_ORE = createBlock("dense_clay_ore", BlockDenseClayOre())
    val LARGE_DENSE_CLAY_ORE = createBlock("large_dense_clay_ore", BlockDenseClayOre())

    private val blocks: MutableMap<String, Block> = HashMap()
    val allBlocks: Map<String, Block> get() = ImmutableMap.copyOf(blocks)

    private fun <T: Block> createBlock(key: String, block: T): T {
        return block.apply {
            setCreativeTab(Clayium.creativeTab)
            setRegistryName(Clayium.MOD_ID, key)
            setTranslationKey("${Clayium.MOD_ID}.$key")
        }
    }

    @SideOnly(Side.CLIENT)
    fun registerItemBlockModels() {
        registerItemModel(CLAY_WORK_TABLE)

        registerItemModel(COMPRESSED_CLAY)
        registerItemModel(ENERGIZED_CLAY)

        registerItemModel(CLAY_ORE)
        registerItemModel(DENSE_CLAY_ORE)
        registerItemModel(LARGE_DENSE_CLAY_ORE)
    }

    @SideOnly(Side.CLIENT)
    private fun registerItemModel(block: Block) {
        for (state in block.blockState.validStates) {
            if (block.blockState.properties.isEmpty()) {
                ModelLoader.setCustomModelResourceLocation(
                    Item.getItemFromBlock(block), 0,
                    ModelResourceLocation(block.registryName!!, "normal")
                )
            } else {
                val meta = block.getMetaFromState(state)
                ModelLoader.setCustomModelResourceLocation(
                    Item.getItemFromBlock(block), meta,
                    ModelResourceLocation(block.registryName!!, "meta=$meta")
                )
            }
        }
    }
}
