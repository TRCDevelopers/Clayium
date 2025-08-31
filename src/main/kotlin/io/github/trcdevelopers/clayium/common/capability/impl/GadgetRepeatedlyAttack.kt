package io.github.trcdevelopers.clayium.common.capability.impl

import io.github.trcdevelopers.clayium.api.capability.IItemGadget
import io.github.trcdevelopers.clayium.api.util.clayiumId

object GadgetRepeatedlyAttack : IItemGadget {
    override val category = clayiumId("repeatedly_attack")
}