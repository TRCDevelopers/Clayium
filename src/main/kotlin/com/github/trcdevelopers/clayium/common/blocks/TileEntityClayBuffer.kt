package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class TileEntityClayBuffer : TileMachineTemp() {

    override lateinit var itemStackHandler: IItemHandler

    var inventoryY: Int = 1
        private set
    var inventoryX: Int = 1
        private set

    override fun initParams(tier: Int, inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        super.initParams(tier, inputModes, outputModes)
        this.inventoryY = when (tier) {
            in 4..7 -> tier - 3
            8, -> 4
            in 9..13 -> 6
            else -> 1
        }
        this.inventoryX = when (tier) {
            in 4..7 -> tier - 2
            in 8..13 -> 9
            else -> 1
        }
        this.itemStackHandler = object : ItemStackHandler(inventoryX * inventoryY) {
            override fun onContentsChanged(slot: Int) = this@TileEntityClayBuffer.markDirty()
        }
    }

}