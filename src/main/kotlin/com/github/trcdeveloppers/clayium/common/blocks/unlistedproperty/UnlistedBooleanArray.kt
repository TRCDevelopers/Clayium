package com.github.trcdeveloppers.clayium.common.blocks.unlistedproperty

import net.minecraftforge.common.property.IUnlistedProperty

class UnlistedBooleanArray(
    private val name: String,
) : IUnlistedProperty<BooleanArray> {
    override fun getName(): String {
        return this.name
    }

    override fun getType(): Class<BooleanArray> {
        return BooleanArray::class.java
    }

    override fun valueToString(value: BooleanArray): String {
        return value.toString()
    }

    override fun isValid(value: BooleanArray): Boolean {
        return value.size == 6
    }
}