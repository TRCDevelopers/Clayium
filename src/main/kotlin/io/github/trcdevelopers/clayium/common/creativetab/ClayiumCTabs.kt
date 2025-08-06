package io.github.trcdevelopers.clayium.common.creativetab

import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.util.toItemStack
import io.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

object ClayiumCTabs {
    // for CreativeTab ordering
    fun init() {}

    val main = BasicCreativeTab(MOD_ID, { ItemStack(Items.CLAY_BALL) })
    val decorations = BasicCreativeTab("$MOD_ID.decorations", { ClayiumBlocks.COMPRESSED_BLOCKS.first().defaultState.toItemStack() })
}