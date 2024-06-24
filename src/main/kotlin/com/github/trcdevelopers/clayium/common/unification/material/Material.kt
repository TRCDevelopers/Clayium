package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import net.minecraft.util.ResourceLocation

data class Material(
    val metaItemSubId: Int,
    val metaItemId: ResourceLocation,
    val properties: MaterialProperties,
    val tier: Int = NO_TIER,
    val colors: IntArray? = null,
) {
    class Builder(
        private val metaItemSubId: Int,
        private val metaItemId: ResourceLocation,
    ) {
        private var properties: MaterialProperties = MaterialProperties()
        private var tier: Int = NO_TIER
        private var colors: IntArray? = null

        fun tier(tier: Int) = apply { this.tier = tier }
        fun colors(vararg colors: Int) = apply { this.colors = colors }

        fun ingot() = apply { properties.setProperty(PropertyKey.INGOT, MaterialProperty.Ingot) }
        fun dust() = apply { properties.setProperty(PropertyKey.DUST, MaterialProperty.Dust) }

        fun matter(texture: String = "matter"): Builder {
            properties.setProperty(PropertyKey.MATTER, MaterialProperty.Matter(texture))
            return this
        }

        fun impureDust(color1: Int, color2: Int, color3: Int): Builder {
            properties.setProperty(PropertyKey.IMPURE_DUST, MaterialProperty.ImpureDust(color1, color2, color3))
            return this
        }

        fun plate(cePerTick: ClayEnergy, requiredTick: Int, tier: Int): Builder {
            properties.setProperty(PropertyKey.PLATE, MaterialProperty.Plate(cePerTick, requiredTick, tier))
            return this
        }

        fun build() = Material(metaItemSubId, metaItemId, properties, tier, colors)
    }
    companion object {
        const val NO_TIER = -1

        inline fun create(metaItemSubId: Int, metaItemId: ResourceLocation, init: Builder.() -> Unit): Material {
            return Builder(metaItemSubId, metaItemId).apply(init).build()
        }
    }
}