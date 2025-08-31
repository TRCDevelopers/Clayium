package io.github.trcdevelopers.clayium.common.capability.impl

import io.github.trcdevelopers.clayium.api.capability.IItemGadget
import io.github.trcdevelopers.clayium.api.util.clayiumId
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import kotlin.math.min

private const val MAX_FOOD_LEVEL = 20

class ClayGadgetAutoEat(
    val economicalMode: Boolean,
) : IItemGadget {
    override val category: ResourceLocation = clayiumId("auto_eat")

    override fun updateInventory(player: EntityPlayer, isRemote: Boolean) {
        if (isRemote) return
        if (!player.foodStats.needFood()) return

        var mostEfficientFoodStack = ItemStack.EMPTY
        var currentFoodEfficiency = 0.0
        for (i in 0..<player.inventory.sizeInventory) {
            val stack = player.inventory.getStackInSlot(i)
            val item = stack.item
            if (item is ItemFood) {
                val playerFoodLevel = player.foodStats.foodLevel
                val itemFoodLevel = item.getHealAmount(stack)
                val actualHealLevel = min(itemFoodLevel, MAX_FOOD_LEVEL - playerFoodLevel)
                if (actualHealLevel <= 0 || (economicalMode && itemFoodLevel != actualHealLevel)) {
                    continue
                }
                val thisFoodEff = actualHealLevel / itemFoodLevel.toDouble()
                if (thisFoodEff > currentFoodEfficiency) {
                    mostEfficientFoodStack = stack
                    currentFoodEfficiency = thisFoodEff
                }
            }
        }
        if (!mostEfficientFoodStack.isEmpty) {
            mostEfficientFoodStack.onItemUseFinish(player.world, player)
            player.inventory.markDirty()
        }
    }
}