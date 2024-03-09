package com.github.trcdevelopers.clayium.common.blocks.machine

import com.github.trcdevelopers.clayium.common.Clayium
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

    val BENDING_MACHINE = createMachine(Name.BENDING, intArrayOf(1, 2, 3, 4, 5, 6, 7, 9)) { tier ->
        TileEntityMachine.create(tier, MachineIoMode.Input.SINGLE, MachineIoMode.Output.SINGLE)
    }

    private fun createMachine(name: String, tiers: IntArray, tileEntityProvider: (Int) -> TileEntityMachine): Map<Int, BlockMachine> {
        val map = Int2ObjectLinkedOpenHashMap<BlockMachine>()
        for (tier in tiers) {
            val block = BlockMachine(tier, tileEntityProvider).apply { setRegistryName(Clayium.MOD_ID, "${name}_tier$tier") }
            map.put(tier, block)
        }
        _machines[name] = map
        return map
    }

    object Name {
        const val BENDING = "bending_machine"
    }
}