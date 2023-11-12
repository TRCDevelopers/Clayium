package com.github.trcdeveloppers.clayium.common.blocks.machine

import com.github.trcdeveloppers.clayium.Clayium.Companion.MOD_ID
import com.github.trcdeveloppers.clayium.common.annotation.CBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry

@CBlock(registryName = "test_single_slot_machine")
class BlockTestSingleSlotMachine : BlockSingleSlotMachine() {

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return TileSingleSlotMachine()
    }

    companion object {
        @JvmStatic
        @GameRegistry.ObjectHolder("$MOD_ID:test_single_slot_machine")
        lateinit var INSTANCE: BlockTestSingleSlotMachine
    }
}