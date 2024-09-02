package com.github.trc.clayium.api.util.registry

import com.github.trc.clayium.api.unification.material.IMaterial
import com.github.trc.clayium.api.util.clayiumId
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