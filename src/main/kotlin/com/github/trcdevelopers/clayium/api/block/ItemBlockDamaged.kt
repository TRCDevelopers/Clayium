package com.github.trcdevelopers.clayium.api.block

import net.minecraft.block.Block
import net.minecraft.item.ItemBlock

class ItemBlockDamaged(block: Block) : ItemBlock(block) {
    override fun getMetadata(damage: Int) = damage
}