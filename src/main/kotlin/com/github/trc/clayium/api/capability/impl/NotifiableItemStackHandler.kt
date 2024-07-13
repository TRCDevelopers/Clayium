package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.metatileentity.MetaTileEntity

class NotifiableItemStackHandler(
    metaTileEntity: MetaTileEntity,
    size: Int,
    private val entitiesToNotify: MutableList<MetaTileEntity>,
    private val isExport: Boolean,
) : ClayiumItemStackHandler(metaTileEntity, size) {

    constructor(metaTileEntity: MetaTileEntity, size: Int, entityToNotify: MetaTileEntity, isExport: Boolean) : this(metaTileEntity, size, mutableListOf(entityToNotify), isExport)

    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        if (isExport) {
            entitiesToNotify.forEach { it.hasNotifiedOutputs = true }
        } else {
            entitiesToNotify.forEach { it.hasNotifiedInputs = true }
        }
    }

    fun addNotifiableEntity(entity: MetaTileEntity) {
        entitiesToNotify.add(entity)
    }

    fun removeNotifiableEntity(entity: MetaTileEntity) {
        entitiesToNotify.remove(entity)
    }
}