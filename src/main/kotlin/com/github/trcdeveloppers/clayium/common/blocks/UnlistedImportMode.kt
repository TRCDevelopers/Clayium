package com.github.trcdeveloppers.clayium.common.blocks

import com.github.trcdeveloppers.clayium.common.blocks.machine.EnumIoMode
import net.minecraftforge.common.property.IUnlistedProperty

class UnlistedImportMode(
    private val name: String,
) : IUnlistedProperty<EnumIoMode> {
    override fun getName(): String {
        return this.name
    }

    override fun getType(): Class<EnumIoMode> {
        return EnumIoMode::class.java
    }

    override fun valueToString(value: EnumIoMode): String {
        return value.name
    }

    override fun isValid(value: EnumIoMode): Boolean {
        return true
    }
}