package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.Clayium.Companion.MOD_ID
import com.github.trcdevelopers.clayium.common.blocks.clay.BlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.clay.BlockEnergizedClay
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.BlockClayWorkTable
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockClayOre
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockDenseClayOre
import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block

/**
 * holds non-functional blocks and BlockClayWorkTable.
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
            setRegistryName(MOD_ID, key)
            setTranslationKey("$MOD_ID.$key")
        }
    }

    fun getBlock(registryName: String): Block? {
        return blocks[registryName]
    }
}
