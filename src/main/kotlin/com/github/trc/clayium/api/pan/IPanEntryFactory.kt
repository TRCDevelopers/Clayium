package com.github.trc.clayium.api.pan

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

interface IPanEntryFactory {
    fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>): IPanEntry?
}