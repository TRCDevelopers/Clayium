package com.github.trc.clayium.util

import com.github.trc.clayium.Bootstrap
import com.github.trc.clayium.api.util.CUtils
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.ItemStackHandler

@Suppress("unused")
class TestCUtils : StringSpec({

    beforeTest {
        Bootstrap.perform()
    }

    "ItemStacks List Serialization" {

        val originalStacks = listOf(
            ItemStack(Items.DIAMOND),
            ItemStack.EMPTY,
            ItemStack.EMPTY,
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

    "IItemHandler Serialization" {
        val originalHandler = ItemStackHandler(5)
        originalHandler.setStackInSlot(0, ItemStack(Items.DIAMOND))
        originalHandler.setStackInSlot(1, ItemStack(Items.IRON_INGOT))
        originalHandler.setStackInSlot(4, ItemStack(Items.GOLD_INGOT, 64))

        val data = NBTTagCompound()
        CUtils.writeItems(originalHandler, "stacks", data)

        val newHandler = ItemStackHandler(5)
        CUtils.readItems(newHandler, "stacks", data)
        ItemStack.areItemStacksEqual(newHandler.getStackInSlot(0), originalHandler.getStackInSlot(0)) shouldBe true
        ItemStack.areItemStacksEqual(newHandler.getStackInSlot(1), originalHandler.getStackInSlot(1)) shouldBe true
        newHandler.getStackInSlot(2).isEmpty shouldBe true
        newHandler.getStackInSlot(3).isEmpty shouldBe true
        ItemStack.areItemStacksEqual(newHandler.getStackInSlot(4), originalHandler.getStackInSlot(4)) shouldBe true
    }
})