package com.github.trcdevelopers.clayium.common.items.handler

import com.github.trcdevelopers.clayium.common.tileentity.TileEntityMachine

class NotifiableItemStackHandler(
    tileEntity: TileEntityMachine,
    size: Int,
    private val entitiesToNotify: MutableList<TileEntityMachine>,
    private val isExport: Boolean,
) : ClayiumItemStackHandler(tileEntity, size) {

    constructor(tileEntity: TileEntityMachine, size: Int, entityToNotify: TileEntityMachine, isExport: Boolean) : this(tileEntity, size, mutableListOf(entityToNotify), isExport)

    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        if (isExport) {
            entitiesToNotify.forEach { it.hasNotifiedOutputs = true }
        } else {
            entitiesToNotify.forEach { it.hasNotifiedInputs = true }
        }
    }

    fun addNotifiableEntity(entity: TileEntityMachine) {
        entitiesToNotify.add(entity)
    }

    fun removeNotifiableEntity(entity: TileEntityMachine) {
        entitiesToNotify.remove(entity)
    }
}