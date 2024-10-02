package com.github.trc.clayium.api.metatileentity.registry

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.jetbrains.annotations.UnmodifiableView

class CMteManager {
    private var currentNetworkId = 0
    private val modId2Registry = Object2ObjectOpenHashMap<String, CMteRegistry>()
    private val networkIdMap = Int2ObjectOpenHashMap<CMteRegistry>()

    /**
     * Gets the registry for the given modId.
     * @return The registry, or null if it doesn't exist
     */
    fun getRegistry(modId: String): CMteRegistry? {
        return modId2Registry[modId]
    }

    /**
     * Creates a new MetaTileEntity registry for the given modId.
     * @throws IllegalArgumentException If a registry for the modId already exists
     * @return The created registry
     */
    fun createRegistry(modId: String): CMteRegistry {
        val existing = modId2Registry[modId]
        if (existing != null) {
            throw IllegalArgumentException("Registry for mod $modId already exists")
        }
        val registry = CMteRegistry(modId, currentNetworkId++)
        modId2Registry[modId] = registry
        networkIdMap[registry.networkId] = registry
        return registry
    }

    fun allRegistries(): @UnmodifiableView Collection<CMteRegistry> {
        return modId2Registry.values
    }
}
