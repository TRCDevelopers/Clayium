package com.github.trc.clayium.api.block

import com.github.trc.clayium.api.util.ITier
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

interface ITieredBlock {
    fun getTier(stack: ItemStack): ITier
    fun getTier(world: IBlockAccess, pos: BlockPos): ITier
}