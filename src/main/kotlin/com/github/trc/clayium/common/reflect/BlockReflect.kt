package com.github.trc.clayium.common.reflect

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.ReflectionHelper

@Suppress("DEPRECATION")
object BlockReflect {
    private val silkTouchDrop by lazy {
        ReflectionHelper.findMethod(
            Block::class.java,
            "getSilkTouchDrop",
            "func_180643_i",
            IBlockState::class.java
        )
    }

    fun getSilkTouchDrop(block: Block, state: IBlockState): ItemStack {
        return silkTouchDrop.invoke(block, state) as ItemStack
    }
}
