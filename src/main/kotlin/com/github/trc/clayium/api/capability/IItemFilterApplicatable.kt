package com.github.trc.clayium.api.capability

import com.github.trc.clayium.common.items.filter.ItemFilterBase
import net.minecraft.util.EnumFacing

/**
 * Use this capability interface to accept Clayium's item filters.
 *
 * Usage overview:
 * - A [IItemFilter] Instance is given on [setFilter], use this for filtering items. [ItemFilterBase] is also given, store it or its registry name for [getFilterItem].
 *   You can write the registry name to NBT for persistence. Example: [com.github.trc.clayium.api.metatileentity.MetaTileEntity.writeToNBT]
 * - [getFilterItem] returns the [ItemFilterBase] for Filter Duplicator.
 * - [clearFilter] just the name implies.
 *
 *
 */
interface IItemFilterApplicatable {
    fun setFilter(side: EnumFacing, filter: IItemFilter, filterItem: ItemFilterBase)
    fun getFilter(side: EnumFacing): IItemFilter?
    fun getFilterItem(side: EnumFacing): ItemFilterBase?
    fun clearFilter(side: EnumFacing)
}