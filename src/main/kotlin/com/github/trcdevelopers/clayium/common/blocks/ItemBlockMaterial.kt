package com.github.trcdevelopers.clayium.common.blocks

import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.common.IRarity

open class ItemBlockMaterial(
    val blockMaterial: BlockMaterialBase
) : ItemBlock(blockMaterial) {
    init { hasSubtypes = true }
    override fun getMetadata(damage: Int) = damage

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return blockMaterial.getCMaterial(stack).tier?.rarity ?: super.getForgeRarity(stack)
    }
}