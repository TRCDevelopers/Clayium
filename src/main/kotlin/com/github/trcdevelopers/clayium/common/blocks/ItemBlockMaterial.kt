package com.github.trcdevelopers.clayium.common.blocks

import net.minecraft.item.ItemBlock

open class ItemBlockMaterial(
    val blockMaterial: BlockMaterialBase
) : ItemBlock(blockMaterial) {
    override fun getMetadata(damage: Int) = damage
}