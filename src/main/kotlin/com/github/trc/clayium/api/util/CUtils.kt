package com.github.trc.clayium.api.util

import com.cleanroommc.modularui.api.drawable.IKey
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.block.ItemBlockMachine
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trc.clayium.common.gui.ResizingTextWidget
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

fun IBlockAccess?.getMetaTileEntity(pos: BlockPos?): MetaTileEntity? {
    if (this == null || pos == null) return null
    return (this.getTileEntity(pos) as? MetaTileEntityHolder)?.metaTileEntity
}

fun ItemStack.copyWithSize(size: Int): ItemStack {
    val stack = copy()
    stack.count = size
    return stack
}

fun ItemStack.canStackWith(other: ItemStack): Boolean {
    return (this.isEmpty || other.isEmpty) || (isItemEqual(other)
            && (!item.isDamageable || itemDamage == other.itemDamage)
            && ItemStack.areItemStackTagsEqual(this, other))
}

fun <T> ItemStack.getCapability(capability: Capability<T>): T? {
    return getCapability(capability, null)
}

fun <T> ItemStack.hasCapability(capability: Capability<T>): Boolean {
    return hasCapability(capability, null)
}

fun IItemHandler.toList(): List<ItemStack> {
    return object : AbstractList<ItemStack>() {
        override val size = slots

        override fun get(index: Int): ItemStack {
            return getStackInSlot(index)
        }
    }
}

fun IBlockState.toItemStack(count: Int = 1): ItemStack {
    return ItemStack(block, count, block.getMetaFromState(this))
}

fun Block.getAsItem(): Item {
    return Item.getItemFromBlock(this)
}

fun TileEntity?.takeIfValid(): TileEntity? {
    return this?.takeUnless { it.isInvalid }
}

fun IKey.asWidgetResizing(): ResizingTextWidget {
    return ResizingTextWidget(this)
}

inline fun <reified E : Enum<E>> E.next(): E {
    val values = enumValues<E>()
    val ordinal = (ordinal + 1) % values.size
    return values[ordinal]
}

fun clayiumId(path: String): ResourceLocation {
    return ResourceLocation(CValues.MOD_ID, path)
}

object CUtils {

    fun writeItems(handler: IItemHandler, tagName: String, tag: NBTTagCompound) {
        val tagList = NBTTagList()
        for (i in 0..<handler.slots) {
            val stack = handler.getStackInSlot(i)
            if (stack.isEmpty) continue

            val stackTag = NBTTagCompound()
            stackTag.setInteger("Slot", i)
            stack.writeToNBT(stackTag)
            tagList.appendTag(stackTag)
        }
        tag.setTag(tagName, tagList)
    }

    fun writeItems(items: List<ItemStack>, tagName: String, tag: NBTTagCompound) {
        val tagList = NBTTagList()
        items.forEachIndexed { i, stack ->
            if (stack.isEmpty) {
                tagList.appendTag(NBTTagCompound())
            } else {
                val stackTag = NBTTagCompound()
                stackTag.setInteger("Slot", i)
                stack.writeToNBT(stackTag)
                tagList.appendTag(stackTag)
            }
        }
        tag.setTag(tagName, tagList)
    }

    fun readItems(handler: IItemHandlerModifiable, tagName: String, tag: NBTTagCompound) {
        if (!tag.hasKey(tagName, Constants.NBT.TAG_LIST)) return
        val tagList = tag.getTagList(tagName, Constants.NBT.TAG_COMPOUND)
        for (i in 0..<tagList.tagCount()) {
            val itemTag = tagList.getCompoundTagAt(i)
            val slot = itemTag.getInteger("Slot").toInt()
            if (slot in 0..<handler.slots) {
                handler.setStackInSlot(slot, ItemStack(itemTag))
            }
        }
    }

    fun readItems(tagName: String, tag: NBTTagCompound): List<ItemStack> {
        val items = mutableListOf<ItemStack>()
        val tagList = tag.getTagList(tagName, Constants.NBT.TAG_COMPOUND)
        for (i in 0..<tagList.tagCount()) {
            val stackTag = tagList.getCompoundTagAt(i)
            if (stackTag.hasKey("Slot"))  {
                items.add(ItemStack(stackTag))
            } else {
                items.add(ItemStack.EMPTY)
            }
        }
        return items
    }

    fun getMetaTileEntity(stack: ItemStack): MetaTileEntity? {
        return if (stack.item is ItemBlockMachine) {
            ClayiumApi.MTE_REGISTRY.getObjectById(stack.itemDamage)
        } else {
            null
        }
    }
}