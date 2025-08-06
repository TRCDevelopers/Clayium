package io.github.trcdevelopers.clayium.api.pan

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.stack.ItemAndMeta

interface IPan {
    fun getDuplicationEntries(): Map<ItemAndMeta, ClayEnergy>
}