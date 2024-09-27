package com.github.trc.clayium.common.creativetab

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.util.toItemStack
import com.github.trc.clayium.common.blocks.ClayiumBlocks

object ClayiumCTabs {
    val decorations = BasicCreativeTab("$MOD_ID.decorations", { ClayiumBlocks.COMPRESSED_BLOCKS.first().defaultState.toItemStack() })
}