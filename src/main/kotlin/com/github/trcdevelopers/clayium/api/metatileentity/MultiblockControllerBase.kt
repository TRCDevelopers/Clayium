package com.github.trcdevelopers.clayium.api.metatileentity

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.util.ResourceLocation


abstract class MultiblockControllerBase(
    metaTileEntityId: ResourceLocation,
    tier: Int,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey) {

    abstract fun isConstructed(): Boolean
    fun onConstructed() {}
    fun onDeconstructed() {}

    override fun update() {
        super.update()
        if (offsetTimer % 20 == 0L) {
            if (isConstructed()) {
                onConstructed()
            } else {
                onDeconstructed()
            }
        }
    }

}