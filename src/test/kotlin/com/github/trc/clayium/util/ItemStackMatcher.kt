package com.github.trc.clayium.util

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

fun haveCount(count: Int) =
    Matcher<ItemStack> { value ->
        MatcherResult(
            value.count == count,
            { "ItemStack should have count $count, but has ${value.count}" },
            { "ItemStack should not have count $count" },
        )
    }

fun haveItem(item: Item) =
    Matcher<ItemStack> { value ->
        MatcherResult(
            value.item == item,
            { "ItemStack should have item $item, but has ${value.item}" },
            { "ItemStack should not have item $item" },
        )
    }

fun beEmpty() =
    Matcher<ItemStack> { value ->
        MatcherResult(
            value.isEmpty,
            { "ItemStack should be empty" },
            { "ItemStack should not be empty" },
        )
    }
