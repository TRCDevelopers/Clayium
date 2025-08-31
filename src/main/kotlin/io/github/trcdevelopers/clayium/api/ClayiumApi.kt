package io.github.trcdevelopers.clayium.api

import io.github.trcdevelopers.clayium.api.events.ClayiumMteRegistryEvent
import io.github.trcdevelopers.clayium.api.metatileentity.registry.CMteManager
import io.github.trcdevelopers.clayium.api.pan.IPanRecipeFactory
import io.github.trcdevelopers.clayium.api.unification.material.CMaterial
import io.github.trcdevelopers.clayium.api.unification.material.registry.CMarkerMaterialRegistry
import io.github.trcdevelopers.clayium.api.util.registry.MaterialRegistry

object ClayiumApi {
    val materialRegistry = MaterialRegistry<CMaterial>(Short.MAX_VALUE.toInt())
    val markerMaterials = CMarkerMaterialRegistry()

    //TODO mutableがそのままpublicになっているのは直す
    val PAN_RECIPE_FACTORIES = mutableListOf<IPanRecipeFactory>()

    /**
     * A Registry of MteRegistries.
     * If you want to create new MteRegistry,
     * listen to [ClayiumMteRegistryEvent]
     * and register your MteRegistry using [ClayiumMteRegistryEvent.mteManager]
     */
    val mteManager: CMteManager = CMteManager()
}