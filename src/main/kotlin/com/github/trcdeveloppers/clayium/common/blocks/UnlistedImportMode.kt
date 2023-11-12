package com.github.trcdeveloppers.clayium.common.blocks

import com.github.trcdeveloppers.clayium.common.blocks.machine.EnumImportMode
import net.minecraftforge.common.property.IUnlistedProperty

class UnlistedImportMode(
    private val name: String,
) : IUnlistedProperty<EnumImportMode> {
    override fun getName(): String {
        return this.name
    }

    override fun getType(): Class<EnumImportMode> {
        return EnumImportMode::class.java
    }

    override fun valueToString(value: EnumImportMode): String {
        return value.name
    }

    override fun isValid(value: EnumImportMode): Boolean {
        return true
    }
}