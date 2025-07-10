package com.github.trc.clayium.datafix.fixable

import com.cleanroommc.modularui.utils.ItemStackItemHandler
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.items.ClayiumItems
import com.github.trc.clayium.datafix.ClayiumDataVersion
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.datafix.IFixableData
import net.minecraftforge.common.util.Constants

class MteHolderMigrator : IFixableData {
    override fun getFixVersion(): Int {
        return ClayiumDataVersion.V1_MORE_FILTERS.ordinal
    }

    override fun fixTagCompound(compound: NBTTagCompound): NBTTagCompound {
        if (compound.getString("id") != clayiumId("metaTileEntityHolder").toString()) return compound

        val mteData = compound.getCompoundTag("metaTileEntityData")
        val traitTag = NBTTagCompound()
        for (facing in EnumFacing.entries) {
            val i = facing.index
            val oldKey = "filterType$i"
            val filterDataKey = "filter$i"
            val newKey = "filterItemRegistryName$i"
            if (mteData.hasKey(oldKey, Constants.NBT.TAG_INT) && mteData.hasKey(filterDataKey, Constants.NBT.TAG_COMPOUND)) {
                // **There is only simpleItemFilter on V0 dataVersion**

                val filterData = mteData.getCompoundTag(filterDataKey)
                val stacks = CUtils.readItems("stacks", filterData)
                val whitelist = filterData.getBoolean("whitelist")
                val sampleStack = ItemStack(ClayiumItems.SIMPLE_ITEM_FILTER)
                val itemStackItemHandler = ItemStackItemHandler(sampleStack, 5 * 2)
                for ((i, stack) in stacks.withIndex()) {
                    itemStackItemHandler.setStackInSlot(i, stack)
                }
                val newTag = sampleStack.tagCompound ?: continue
                newTag.setBoolean("isWhiteList", whitelist)
                traitTag.setString(newKey, ClayiumItems.SIMPLE_ITEM_FILTER.registryName!!.toString())
                traitTag.setTag("filterTag$i", newTag)
                mteData.removeTag(oldKey)
                mteData.removeTag("filter$i")
            }
        }
        mteData.setTag(clayiumId("item_filter_holder").toString(), traitTag)
        return compound
    }
}