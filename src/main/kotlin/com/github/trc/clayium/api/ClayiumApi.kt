package com.github.trc.clayium.api

import com.github.trc.clayium.api.events.ClayiumMteRegistryEvent
import com.github.trc.clayium.api.metatileentity.registry.CMteManager
import com.github.trc.clayium.api.pan.IPanRecipeFactory
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.registry.CMarkerMaterialRegistry
import com.github.trc.clayium.api.util.registry.MaterialRegistry

object ClayiumApi {
    val materialRegistry = MaterialRegistry<CMaterial>(Short.MAX_VALUE.toInt())
    val markerMaterials = CMarkerMaterialRegistry()

    val PAN_RECIPE_FACTORIES = mutableListOf<IPanRecipeFactory>()

    /**
     * A Registry of MteRegistries.
     * If you want to create new MteRegistry,
     * listen to [ClayiumMteRegistryEvent]
     * and register your MteRegistry using [ClayiumMteRegistryEvent.mteManager]
     */
    val mteManager: CMteManager = CMteManager()
}