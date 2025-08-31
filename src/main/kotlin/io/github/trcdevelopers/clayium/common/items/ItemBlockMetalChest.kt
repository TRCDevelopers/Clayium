package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.common.blocks.metalchest.BlockMetalChest
import io.github.trcdevelopers.clayium.common.util.SidelessI18n
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.common.IRarity

class ItemBlockMetalChest(
    val blockMetalChest: BlockMetalChest,
) : ItemBlock(blockMetalChest) {

    init {
        this.hasSubtypes = true
    }

    override fun getMetadata(damage: Int) = damage

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return SidelessI18n.format("tile.clayium.metal_chest", SidelessI18n.format(blockMetalChest.getCMaterial(stack).translationKey))
    }

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return blockMetalChest.getCMaterial(stack).tier?.rarity ?: super.getForgeRarity(stack)
    }
}