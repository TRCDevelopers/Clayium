package io.github.trcdevelopers.clayium.common.reflect

import io.github.trcdevelopers.clayium.mixins.minecraft.BlockInvoker
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack

//TODO remove this
object BlockReflect {
    fun getSilkTouchDrop(block: Block, state: IBlockState): ItemStack {
        return (block as BlockInvoker).callGetSilkTouchDrop(state)
    }
}