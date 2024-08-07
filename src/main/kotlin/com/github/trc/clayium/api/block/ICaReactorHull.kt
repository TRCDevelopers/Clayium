package com.github.trc.clayium.api.block

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

interface ICaReactorHull {
    fun getCaRank(stack: ItemStack): Int
    fun getCaRank(world: IBlockAccess, pos: BlockPos): Int
}