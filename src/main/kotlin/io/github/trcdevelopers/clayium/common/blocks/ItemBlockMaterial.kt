package io.github.trcdevelopers.clayium.common.blocks

import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.blocks.material.BlockMaterialBase
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.common.IRarity

open class ItemBlockMaterial(
    val blockMaterial: BlockMaterialBase,
    val orePrefix: OrePrefix,
) : ItemBlock(blockMaterial) {
    init { hasSubtypes = true }
    override fun getMetadata(damage: Int) = damage

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return orePrefix.getLocalizedName(blockMaterial.getCMaterial(stack))
    }

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return blockMaterial.getCMaterial(stack).tier?.rarity ?: super.getForgeRarity(stack)
    }
}