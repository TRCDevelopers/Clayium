package io.github.trcdevelopers.clayium.util

import io.github.trcdevelopers.clayium.Bootstrap
import io.github.trcdevelopers.clayium.common.util.merge
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.should
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

class TestItemStackUtils : StringSpec({

    beforeTest {
        Bootstrap.perform()
    }

    "List<ItemStack>.merge should return empty list when input is empty" {
        val onlyEmpty = listOf(ItemStack.EMPTY)
        val result = onlyEmpty.merge()
        result shouldHaveSize 0
    }

    "List<ItemStack>.merge should return single item when input has one non-empty stack" {
        val onlyOne = listOf(ItemStack(Items.CLAY_BALL, 1))
        val result = onlyOne.merge()
        result shouldHaveSize 1
        result shouldNotContain ItemStack.EMPTY
        result[0] should haveCount(1)
        result[0] should haveItem(Items.CLAY_BALL)
    }

    "List<ItemStack>.merge should merge identical stacks" {
        val stacks = listOf(
            ItemStack(Items.CLAY_BALL, 1),
            ItemStack(Items.CLAY_BALL, 2),
            ItemStack(Items.CLAY_BALL, 3)
        )
        val result = stacks.merge()
        result shouldHaveSize 1
        result shouldNotContain ItemStack.EMPTY
        result[0] should haveCount(6)
        result[0] should haveItem(Items.CLAY_BALL)
    }

    "List<ItemStack>.merge should merge with big stacks" {
        val stacks = listOf(
            ItemStack(Items.CLAY_BALL, 63),
            ItemStack(Items.CLAY_BALL, 63),
            ItemStack(Items.CLAY_BALL, 3)
        )
        val result = stacks.merge()
        result shouldHaveSize 3
        result shouldNotContain ItemStack.EMPTY
        result[0] should haveCount(64)
        result[0] should haveItem(Items.CLAY_BALL)
        result[1] should haveCount(64)
        result[1] should haveItem(Items.CLAY_BALL)
        result[2] should haveCount(1)
        result[2] should haveItem(Items.CLAY_BALL)
    }

    "List<ItemStack>.merge should not merge different items" {
        val stacks = listOf(
            ItemStack(Items.CLAY_BALL, 1),
            ItemStack(Items.GOLD_INGOT, 2)
        )
        val result = stacks.merge()
        result shouldHaveSize 2
        result shouldNotContain ItemStack.EMPTY
        result should containItemStack(ItemStack(Items.CLAY_BALL, 1))
        result should containItemStack(ItemStack(Items.GOLD_INGOT, 2))
    }

    "List<ItemStack>.merge should not modify original stacks" {
        val original = listOf(
            ItemStack(Items.CLAY_BALL, 1),
            ItemStack(Items.CLAY_BALL, 2)
        )
        val result = original.merge()
        result shouldHaveSize 1
        result shouldNotContain ItemStack.EMPTY
        original[0] should haveCount(1)
        original[1] should haveCount(2)
    }
})