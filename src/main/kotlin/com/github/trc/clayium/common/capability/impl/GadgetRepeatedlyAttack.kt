package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.capability.IItemGadget
import com.github.trc.clayium.api.util.clayiumId

object GadgetRepeatedlyAttack : IItemGadget {
    override val category = clayiumId("repeatedly_attack")
}