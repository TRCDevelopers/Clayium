package com.github.trcdevelopers.clayium.api

import com.github.trcdevelopers.clayium.api.block.BlockMachine
import com.github.trcdevelopers.clayium.api.block.ItemBlockMachine
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.registry.CRegistry
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.util.ResourceLocation

object ClayiumApi {
    val BLOCK_MACHINE: BlockMachine = BlockMachine().apply { setRegistryName(clayiumId("machine")) }
    val ITEM_BLOCK_MACHINE: ItemBlockMachine = ItemBlockMachine(BLOCK_MACHINE).apply { setRegistryName(clayiumId("machine")) }

    val MTE_REGISTRY = CRegistry<ResourceLocation, MetaTileEntity>(Short.MAX_VALUE.toInt())
    val materialRegistry = CRegistry<ResourceLocation, Material>(Short.MAX_VALUE.toInt())
}