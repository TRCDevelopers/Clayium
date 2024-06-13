package com.github.trcdevelopers.clayium.util

import com.github.trcdevelopers.clayium.Bootstrap
import com.github.trcdevelopers.clayium.api.util.CUtils
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

@Suppress("unused")
class TestCUtils : StringSpec({

    beforeTest {
        Bootstrap.perform()
    }

    "ItemStacks List Serialization" {

        val originalStacks = listOf(
            ItemStack(Items.DIAMOND),
            ItemStack(Items.IRON_INGOT),
            ItemStack(Items.GOLD_INGOT)
        )

        val data = NBTTagCompound()
        CUtils.writeItems(originalStacks, "stacks", data)
        val deserialized = CUtils.readItems("stacks", data)

        deserialized.shouldHaveSize(originalStacks.size)
        deserialized.forEachIndexed { i, stack ->
            ItemStack.areItemStacksEqual(stack, originalStacks[i]) shouldBe true
        }
    }
})