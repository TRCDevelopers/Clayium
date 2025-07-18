package com.github.trc.clayium.common.items

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.common.blocks.metalchest.BlockMetalChest
import com.github.trc.clayium.common.util.SidelessI18n
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.common.IRarity

class ItemBlockMetalChest(
    val blockMetalChest: BlockMetalChest,
) : ItemBlock(blockMetalChest) {

    init {
        this.hasSubtypes = true
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack?>) {
        if (this.isInCreativeTab(tab)) {
            for (material in ClayiumApi.materialRegistry) {
                if (BlockMetalChest.metalChestConfig[material.materialId] != null) {
                    items.add(ItemStack(this, 1, material.metaItemSubId))
                }
            }
        }
    }

    override fun getMetadata(damage: Int) = damage

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return SidelessI18n.format("tile.clayium.metal_chest", SidelessI18n.format(blockMetalChest.getCMaterial(stack).translationKey))
    }

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return blockMetalChest.getCMaterial(stack).tier?.rarity ?: super.getForgeRarity(stack)
    }
}