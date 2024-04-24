package com.github.trcdevelopers.clayium.common.util

import com.google.common.base.CaseFormat
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
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

    fun writeItems(handler: IItemHandlerModifiable, tagName: String, tag: NBTTagCompound) {
        val tagList = NBTTagList()
        for (i in 0..<handler.slots) {
            val stack = handler.getStackInSlot(i)
            if (!stack.isEmpty) {
                val itemTag = NBTTagCompound()
                itemTag.setByte("Slot", i.toByte())
                stack.writeToNBT(itemTag)
                tagList.appendTag(itemTag)
            }
        }
        tag.setTag(tagName, tagList)
    }

    fun readItems(handler: IItemHandlerModifiable, tagName: String, tag: NBTTagCompound) {
        val tagList = tag.getTagList(tagName, 10)
        for (i in 0..<tagList.tagCount()) {
            val itemTag = tagList.getCompoundTagAt(i)
            val slot = itemTag.getByte("Slot").toInt()
            if (slot in 0 until handler.slots) {
                handler.setStackInSlot(slot, ItemStack(itemTag))
            }
        }
    }

    fun insertToHandler(handler: IItemHandlerModifiable, stacks: List<ItemStack>) {
        for (stack in stacks) {
            var remain: ItemStack = stack
            for (i in 0..<handler.slots) {
                remain = handler.insertItem(i, remain, false)
                if (remain.isEmpty) break
            }
        }
    }

    fun handlerToList(handler: IItemHandlerModifiable): List<ItemStack> {
        return object : AbstractList<ItemStack>() {
            override val size = handler.slots

            override fun get(index: Int): ItemStack {
                return handler.getStackInSlot(index)
            }

        }
    }
}