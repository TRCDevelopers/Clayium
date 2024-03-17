package com.github.trcdevelopers.clayium.common.items

import com.github.trcdevelopers.clayium.common.interfaces.ITiered
import com.github.trcdevelopers.clayium.common.util.CUtils
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.common.IRarity

/**
 * [ItemBlock.getForgeRarity] doesn't override [Item.getForgeRarity] because [Block] doesn't have a rarity.
 * So this class will address that issue.
 */
class ItemBlockTiered<T>(block: T) : ItemBlock(block), ITiered
        where T : Block,
              T : ITiered {
    override val tier: Int = block.tier

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return CUtils.rarityBy(tier)
    }
}