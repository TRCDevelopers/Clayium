package io.github.trcdevelopers.clayium.api.capability

import io.github.trcdevelopers.clayium.common.items.filter.ItemFilterBase
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

/**
 * Capability interface for blocks.
 * Use this to accept Clayium's item filters.
 *
 * Usage overview:
 * - An [IItemFilter] Instance is given on [setFilter], use the given filter for filtering items. [ItemFilterBase] is also given, store it or its registry name for [getFilterItem].
 *   You can write the registry name to NBT for persistence. Example: [io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterHolderTrait.serializeNBT]
 * - [getFilterItem] returns the [ItemFilterBase] for the Filter Duplicator.
 * - [clearFilter] just the name implies.
 */
interface IItemFilterApplicatable {
    /**
     * @param filterItem the filter item used. you can get a registry name from this instance.
     * @param stackTag the NBTTagCompound of the used ItemStack. you can get a Filter instance by creating an ItemStack, setting the NBTTag, and calling [ItemFilterBase.createItemFilter].
     * this tag is copied, so you can safely edit this if you want.
     */
    fun setFilter(side: EnumFacing, filter: IItemFilter, filterItem: ItemFilterBase, stackTag: NBTTagCompound?)
    fun getFilter(side: EnumFacing): IItemFilter?

    /**
     * for the [io.github.trcdevelopers.clayium.common.items.filter.ItemFilterDuplicator].
     *
     * returned stack must have an [ItemFilterBase] as its item and appropriate [NBTTagCompound].
     */
    fun createFilterStack(side: EnumFacing): ItemStack?
    fun clearFilter(side: EnumFacing)
}