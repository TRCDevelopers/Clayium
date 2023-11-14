package com.github.trcdeveloppers.clayium.common.blocks.machine

import com.github.trcdeveloppers.clayium.common.annotation.CBlock
import com.github.trcdeveloppers.clayium.common.annotation.LoadWithCustomLoader
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

@LoadWithCustomLoader
@CBlock(registryName = "test_single_slot_machine", tiers = [5])
class BlockTestSingleSlotMachine(
    private val tier: Int,
) : BlockSingleSlotMachine() {

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return TileSingleSlotMachine()
    }
}