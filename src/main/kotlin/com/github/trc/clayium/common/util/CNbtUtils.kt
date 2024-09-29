package com.github.trc.clayium.common.util

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.IItemHandler

object CNbtUtils {
    fun handleInvSizeDifference(world: World, pos: BlockPos, oldMteData: NBTTagCompound, key: String, thisInventory: IItemHandler) {
        val inventoryData = oldMteData.getTagList(key, Constants.NBT.TAG_COMPOUND)
        val oldSlots = inventoryData.tagCount()
        val thisInvSize = thisInventory.slots
        if (oldSlots > thisInvSize) {
            for (i in thisInvSize..<oldSlots) {
                val itemTag = inventoryData.getCompoundTagAt(i)
                val stack = ItemStack(itemTag)
                Block.spawnAsEntity(world, pos, stack)
            }
        }
    }
}