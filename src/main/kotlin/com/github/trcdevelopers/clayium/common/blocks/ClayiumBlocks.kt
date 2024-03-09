package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.clay.BlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.clay.BlockEnergizedClay
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer.BlockClayBuffer
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.BlockClayWorkTable
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockClayOre
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockDenseClayOre
import com.google.common.collect.ImmutableMap
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import net.minecraft.block.Block

/**
 * holds non-functional blocks and non-BlockMachine machines (e.g. ClayWorkTable, ClayBuffer)
 * for functional blocks, see [MachineBlocks]
 */
object ClayiumBlocks {

    val CLAY_WORK_TABLE = createBlock("clay_work_table", BlockClayWorkTable())

    /**
     * buffer does not have facing property, so it is not a BlockMachine
     */
    val BUFFER: Map<Int, BlockClayBuffer> = Int2ObjectLinkedOpenHashMap<BlockClayBuffer>().also {
        for (tier in 4..13) {
            val name = "clay_buffer_tier$tier"
            it.put(tier, BlockClayBuffer(tier).apply {
                setCreativeTab(Clayium.creativeTab)
                setRegistryName(Clayium.MOD_ID, name)
                setTranslationKey("${Clayium.MOD_ID}.$name")
            })
        }
    }

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

    fun getBlock(registryName: String): Block? {
        return blocks[registryName]
    }
}
