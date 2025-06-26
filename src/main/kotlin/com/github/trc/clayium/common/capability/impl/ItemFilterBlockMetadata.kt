package com.github.trc.clayium.common.capability.impl

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ItemFilterBlockMetadata(
    blockMetadata: String = "",
) : ItemFilterDamageValue(blockMetadata) {
    override fun testBlock(world: World, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        val meta = state.block.getMetaFromState(state)
        return regex.matches(meta.toString())
    }
}