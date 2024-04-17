package com.github.trcdevelopers.clayium.common.util

import com.google.common.base.CaseFormat
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraftforge.common.IRarity
import net.minecraftforge.items.IItemHandlerModifiable

object CUtils {
    fun toUpperCamel(snakeCase: String): String {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, snakeCase)
    }

    fun toLowerSnake(camel: String): String {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel)
    }

    fun rarityBy(tier: Int): IRarity {
        return when (tier) {
                4, 5, 6, 7 -> EnumRarity.UNCOMMON
                8, 9, 10, 11 -> EnumRarity.RARE
                12, 13, 14, 15 -> EnumRarity.EPIC
                else -> EnumRarity.COMMON
        }
    }

    internal fun IItemHandlerModifiable.listView(): List<ItemStack> {
        return object : AbstractList<ItemStack>() {
            override val size = this@listView.slots

            override fun get(index: Int): ItemStack {
                return this@listView.getStackInSlot(index)
            }

        }
    }
}