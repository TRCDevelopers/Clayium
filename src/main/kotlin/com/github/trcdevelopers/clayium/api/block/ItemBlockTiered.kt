package com.github.trcdevelopers.clayium.api.block

import com.github.trcdevelopers.clayium.api.item.ITieredItem
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack

class ItemBlockTiered<T>(
    private val tieredBlock: T,
    hasSubTypes: Boolean = true,
) : ItemBlock(tieredBlock), ITieredItem
        where T : Block, T : ITieredBlock {
    init {
        hasSubtypes = hasSubTypes
    }
    override fun getMetadata(damage: Int) = if (hasSubtypes) damage else 0
    override fun getForgeRarity(stack: ItemStack) = tieredBlock.getTier(stack).rarity
    override fun getTier(stack: ItemStack) = tieredBlock.getTier(stack)
}