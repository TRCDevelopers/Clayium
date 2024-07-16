package com.github.trc.clayium.api.pan

import net.minecraft.item.ItemStack

interface IPanEntryFactory {
    fun getEntry(stacks: Collection<ItemStack>): IPanEntry
}