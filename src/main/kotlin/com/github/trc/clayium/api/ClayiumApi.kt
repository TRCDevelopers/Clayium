package com.github.trc.clayium.api

import com.github.trc.clayium.api.block.BlockMachine
import com.github.trc.clayium.api.block.ItemBlockMachine
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.pan.IPanRecipeFactory
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.registry.CMarkerMaterialRegistry
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.registry.CRegistry
import net.minecraft.util.ResourceLocation

object ClayiumApi {
    val BLOCK_MACHINE: BlockMachine = BlockMachine().apply { setRegistryName(clayiumId("machine")) }
    val ITEM_BLOCK_MACHINE: ItemBlockMachine = ItemBlockMachine(BLOCK_MACHINE).apply { setRegistryName(clayiumId("machine")) }

    val MTE_REGISTRY = CRegistry<ResourceLocation, MetaTileEntity>(Short.MAX_VALUE.toInt())
    val materialRegistry = CRegistry<ResourceLocation, CMaterial>(Short.MAX_VALUE.toInt())
    val markerMaterials = CMarkerMaterialRegistry()

    val PAN_RECIPE_FACTORIES = mutableListOf<IPanRecipeFactory>()
}