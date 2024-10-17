package com.github.trc.clayium.common.items.filter

import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.capability.impl.SimpleItemFilter

enum class FilterType(
    val id: Int,
    val factory: () -> IItemFilter,
) {
    SIMPLE(0, { SimpleItemFilter() }),
    ;

    companion object {
        fun byId(id: Int): FilterType {
            return entries[id]
        }
    }
}
