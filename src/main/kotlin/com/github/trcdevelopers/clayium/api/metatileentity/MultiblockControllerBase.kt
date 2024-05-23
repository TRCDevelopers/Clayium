package com.github.trcdevelopers.clayium.api.metatileentity

import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.IMultiblockPart
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.util.ResourceLocation


abstract class MultiblockControllerBase(
    metaTileEntityId: ResourceLocation,
    tier: Int,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey) {

    protected val multiblockParts = mutableListOf<IMultiblockPart>()
    private var structureFormed = false

    abstract fun isConstructed(): Boolean
    fun onConstructed() {
        multiblockParts.forEach { it.addToMultiblock(this) }
    }
    fun onDeconstructed() {
        multiblockParts.forEach { it.removeFromMultiblock(this) }
        multiblockParts.clear()
    }

    override fun update() {
        super.update()
        if (offsetTimer % 20 == 0L) {
            val constructed = isConstructed()
            if (constructed != structureFormed) {
                if (constructed) {
                    onConstructed()
                } else {
                    onDeconstructed()
                }
                structureFormed = constructed
            }
        }
    }
}