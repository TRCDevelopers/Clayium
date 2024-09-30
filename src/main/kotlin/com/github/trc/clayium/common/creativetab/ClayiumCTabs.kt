package com.github.trc.clayium.common.creativetab

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.util.toItemStack
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

object ClayiumCTabs {
    // for CreativeTab ordering
    fun init() {}

    val main = BasicCreativeTab(MOD_ID, { ItemStack(Items.CLAY_BALL) })
    val decorations = BasicCreativeTab("$MOD_ID.decorations", { ClayiumBlocks.COMPRESSED_BLOCKS.first().defaultState.toItemStack() })
}