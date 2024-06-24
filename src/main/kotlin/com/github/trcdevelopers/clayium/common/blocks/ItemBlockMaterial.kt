package com.github.trcdevelopers.clayium.common.blocks

import net.minecraft.item.ItemBlock

class ItemBlockMaterial(
    val block: BlockMaterialBase
) : ItemBlock(block) {
    override fun getMetadata(damage: Int) = damage
}