package com.github.trc.clayium.api

import com.github.trc.clayium.api.block.BlockMachine
import com.github.trc.clayium.api.block.ItemBlockMachine
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.pan.IPanRecipeFactory
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.registry.CRegistry
import com.github.trc.clayium.common.unification.material.Material
import net.minecraft.util.ResourceLocation

object ClayiumApi {
    val BLOCK_MACHINE: BlockMachine = BlockMachine().apply { setRegistryName(clayiumId("machine")) }
    val ITEM_BLOCK_MACHINE: ItemBlockMachine = ItemBlockMachine(BLOCK_MACHINE).apply { setRegistryName(clayiumId("machine")) }

    val MTE_REGISTRY = CRegistry<ResourceLocation, MetaTileEntity>(Short.MAX_VALUE.toInt())
    val materialRegistry = CRegistry<ResourceLocation, Material>(Short.MAX_VALUE.toInt())

    val PAN_RECIPE_FACTORIES = mutableListOf<IPanRecipeFactory>()
}