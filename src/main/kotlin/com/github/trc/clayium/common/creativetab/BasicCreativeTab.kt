package com.github.trc.clayium.common.creativetab

import com.github.trc.clayium.api.util.CLog
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack

class BasicCreativeTab(
    tabName: String,
    private val iconSupplier: () -> ItemStack,
    private val hasSearchBar: Boolean = false,
) : CreativeTabs(tabName) {
    override fun createIcon(): ItemStack {
        val stack = iconSupplier()
        if (stack.isEmpty) {
            CLog.error("Icon supplier for creative tab $tabLabel returned an empty item stack.")
        }
        return stack
    }
}