package com.github.trc.clayium.api.pan

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

interface IPanEntryFactory {
    fun matches(world: IBlockAccess, pos: BlockPos): Boolean
    fun getEntry(stacks: List<ItemStack>): IPanEntry?
}