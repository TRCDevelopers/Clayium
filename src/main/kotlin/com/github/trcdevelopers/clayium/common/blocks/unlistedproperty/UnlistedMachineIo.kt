package com.github.trcdevelopers.clayium.common.blocks.unlistedproperty

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraftforge.common.property.IUnlistedProperty

class UnlistedMachineIo(
    private val name: String,
) : IUnlistedProperty<List<MachineIoMode>> {
    override fun getName(): String {
        return this.name
    }

    @Suppress("UNCHECKED_CAST")
    override fun getType(): Class<List<MachineIoMode>> {
        return List::class.java as Class<List<MachineIoMode>>
    }

    override fun valueToString(value: List<MachineIoMode>): String {
        return value.toString()
    }

    override fun isValid(value: List<MachineIoMode>): Boolean {
        return value.size == 6
    }
}