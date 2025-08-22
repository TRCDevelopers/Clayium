package io.github.trcdevelopers.clayium.common.capability.impl

import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IMarkDirty
import net.minecraftforge.fluids.FluidTank

class ClayiumFluidTank(
    private val notifiable: IMarkDirty,
    capacity: Int,
) : FluidTank(capacity) {
    override fun onContentsChanged() {
        super.onContentsChanged()
        notifiable.markAsDirty()
    }
}