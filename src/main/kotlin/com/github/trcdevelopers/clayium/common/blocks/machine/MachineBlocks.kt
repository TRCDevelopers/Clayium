package com.github.trcdevelopers.clayium.common.blocks.machine

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileClayBuffer
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileMachine
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileSingle2SingleMachine
import com.github.trcdevelopers.clayium.common.recipe.CRecipes
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

object MachineBlocks {

    private val _machines = Object2ObjectOpenHashMap<String, Map<Int, BlockMachine>>()
    val ALL_MACHINES: Map<String, Map<Int, BlockMachine>> get() = _machines

    /**
     * @param name available names are defined in [Name]
     * @throws IllegalArgumentException if the machine or its tier does not exist
     */
    fun getMachine(name: String, tier: Int): BlockMachine {
        val machines = _machines[name] ?: throw IllegalArgumentException("$name does not exist")
        return machines[tier] ?: throw IllegalArgumentException("$name exists, but tier$tier does not. available tiers: ${machines.keys.joinToString()}")
    }

    val CLAY_BUFFER = createMachine(Name.BUFFER, (4..13).toIntArray(), TileClayBuffer::create)

    val BENDING_MACHINE = createMachine(Name.BENDING, intArrayOf(1, 2, 3, 4, 5, 6, 7, 9)) { tier ->
        TileSingle2SingleMachine.create(tier, CRecipes.BENDING)
    }

    private fun createMachine(name: String, tiers: IntArray, tileEntityProvider: (Int) -> TileMachine): Map<Int, BlockMachine> {
        val map = Int2ObjectLinkedOpenHashMap<BlockMachine>()
        for (tier in tiers) {
            val block = BlockMachine(name, tier, tileEntityProvider).apply { setRegistryName(Clayium.MOD_ID, "${name}_tier$tier") }
            map.put(tier, block)
        }
        _machines[name] = map
        return map
    }

    private fun IntRange.toIntArray(): IntArray {
        if (isEmpty()) return IntArray(0)
        val array = IntArray(last - first + 1)
        for (i in this) {
            array[i - first] = i
        }
        return array
    }

    object Name {
        const val BUFFER = "clay_buffer"
        const val BENDING = "bending_machine"
    }
}