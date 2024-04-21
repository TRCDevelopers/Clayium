package com.github.trcdevelopers.clayium.common.tileentity

import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler

class TileEntityClayBuffer : TileEntityMachine() {

    override lateinit var inputInventory: IItemHandlerModifiable
    override lateinit var outputInventory: IItemHandlerModifiable
    override lateinit var combinedInventory: IItemHandler

    private var inventoryRowSize: Int = 0
    private var inventoryColumnSize: Int = 0

    override fun initializeByTier(tier: Int) {
        inventoryRowSize = when (tier) {
            in 4..7 -> tier - 3
            8, -> 4
            in 9..13 -> 6
            else -> 1
        }
        inventoryColumnSize = when (tier) {
            in 4..7 -> tier - 2
            in 8..13 -> 9
            else -> 1
        }
        val inventory = ItemStackHandler(inventoryColumnSize * inventoryRowSize)
        inputInventory = inventory
        outputInventory = inventory
        combinedInventory = inventory
    }
}