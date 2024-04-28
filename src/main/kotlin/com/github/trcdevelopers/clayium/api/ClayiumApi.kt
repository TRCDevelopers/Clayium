package com.github.trcdevelopers.clayium.api

import com.github.trcdevelopers.clayium.api.block.BlockMachine
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.registry.CRegistry
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation

object ClayiumApi {
    val BLOCK_MACHINE: Block = BlockMachine().setRegistryName(CValues.MOD_ID, "machine")

    val MTE_REGISTRY = CRegistry<ResourceLocation, MetaTileEntity>(Short.MAX_VALUE.toInt())
}