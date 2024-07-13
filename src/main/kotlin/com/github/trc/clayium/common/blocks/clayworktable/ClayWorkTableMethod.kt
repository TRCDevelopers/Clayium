package com.github.trc.clayium.common.blocks.clayworktable

import com.github.trc.clayium.common.items.ClayiumItems
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

enum class ClayWorkTableMethod(
    val id: Int,
    val requiredTools: List<Item> = emptyList(),
) {
    ROLLING_HAND(0),
    PUNCH(1),
    ROLLING_PIN(2, listOf(ClayiumItems.CLAY_ROLLING_PIN)),
    CUT_PLATE(3, listOf(ClayiumItems.CLAY_SLICER, ClayiumItems.CLAY_SPATULA)),
    CUT_DISC(4, listOf(ClayiumItems.CLAY_SPATULA)),
    CUT(5, listOf(ClayiumItems.CLAY_SLICER, ClayiumItems.CLAY_SPATULA)),
    ;

    fun isValidTool(tool: ItemStack): Boolean {
        return requiredTools.isEmpty() || tool.item in requiredTools
    }

    companion object {

        val ids = entries.map { it.id }.toIntArray()

        fun fromId(id: Int): ClayWorkTableMethod? {
            return when (id) {
                0 -> ROLLING_HAND
                1 -> PUNCH
                2 -> ROLLING_PIN
                3 -> CUT_PLATE
                4 -> CUT_DISC
                5 -> CUT
                else -> null
            }
        }
    }
}
