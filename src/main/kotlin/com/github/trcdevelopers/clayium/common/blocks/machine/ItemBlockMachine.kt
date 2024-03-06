package com.github.trcdevelopers.clayium.common.blocks.machine

import net.minecraft.item.ItemBlock

class ItemBlockMachine(block: BlockMachine) : ItemBlock(block) {
    init {
        setHasSubtypes(true)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}