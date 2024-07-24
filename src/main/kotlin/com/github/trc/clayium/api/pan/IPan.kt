package com.github.trc.clayium.api.pan

import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.unification.stack.ItemAndMeta

interface IPan : IPanNotifiable {
    fun getDuplicationEntries(): Map<ItemAndMeta, ClayEnergy>
}