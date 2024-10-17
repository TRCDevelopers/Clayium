package com.github.trc.clayium.api.unification.material

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.client.renderer.block.model.ModelResourceLocation

sealed interface MaterialProperty {
    fun verify(material: CMaterial): Boolean = true

    data object Ingot : MaterialProperty

    data object Dust : MaterialProperty

    class Matter(
        texture: String = "matter",
    ) : MaterialProperty {
        val modelLocation = ModelResourceLocation(clayiumId("colored/$texture"), "inventory")
    }

    class Plate(
        val cePerTick: ClayEnergy,
        val requiredTick: Int,
        val tier: Int,
    ) : MaterialProperty {
        override fun verify(material: CMaterial) =
            material.hasProperty(CPropertyKey.Companion.INGOT) ||
                material.hasProperty(CPropertyKey.Companion.MATTER)
    }

    class ImpureDust(
        private vararg val colors: Int,
    ) : MaterialProperty {

        init {
            require(colors.size == 3) { "ImpureDust must have 3 color layers" }
        }

        override fun verify(material: CMaterial) = material.hasProperty(CPropertyKey.Companion.DUST)

        fun getColor(i: Int) = colors[i]
    }
}

class Clay(val compressionLevel: Int, val compressedInto: CMaterial?, val energy: ClayEnergy?) :
    MaterialProperty

class ClaySmelting(val factor: Double, val tier: Int, val duration: Int) : MaterialProperty {
    constructor(tier: Int, duration: Int) : this(1.0, tier, duration)
}

class BlastSmelting(val factor: Double, val tier: Int, val duration: Int) : MaterialProperty {
    constructor(tier: Int, duration: Int) : this(1.0, tier, duration)
}
