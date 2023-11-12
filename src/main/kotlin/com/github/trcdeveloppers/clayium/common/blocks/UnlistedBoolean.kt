package com.github.trcdeveloppers.clayium.common.blocks

import net.minecraftforge.common.property.IUnlistedProperty

class UnlistedBoolean(
    private val name: String,
) : IUnlistedProperty<Boolean> {
    override fun getName(): String {
        return this.name
    }

    override fun getType(): Class<Boolean> {
        return Boolean::class.javaObjectType
    }

    override fun valueToString(value: Boolean): String {
        return if (value) "true" else "false"
    }

    override fun isValid(value: Boolean): Boolean {
        return true
    }
}