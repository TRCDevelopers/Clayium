package io.github.trcdevelopers.clayium.api.util.registry

import io.github.trcdevelopers.clayium.api.unification.material.IMaterial
import io.github.trcdevelopers.clayium.api.util.clayiumId
import net.minecraft.util.ResourceLocation

class MaterialRegistry<V: IMaterial>(
    maxId: Int,
) : CRegistry<ResourceLocation, V>(maxId) {
    /**
     * get material by name without mod id.
     * auto completes mod id "clayium".
     */
    fun get(name: String): V? {
        return getObject(clayiumId(name))
    }
}