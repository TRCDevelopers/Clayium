package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import net.minecraft.util.ResourceLocation

data class Material(
    val metaItemSubId: Int,
    val materialId: ResourceLocation,
    val properties: MaterialProperties,
    val tier: ITier? = null,
    val colors: IntArray? = null,
) : Comparable<Material> {

    override fun compareTo(other: Material): Int {
        return metaItemSubId.compareTo(other.metaItemSubId)
    }

    fun hasProperty(key: PropertyKey<*>) = properties.hasProperty(key)
    fun <T : MaterialProperty> getProperty(key: PropertyKey<T>) = properties.getProperty(key)

    class Builder(
        private val metaItemSubId: Int,
        private val metaItemId: ResourceLocation,
    ) {
        private var properties: MaterialProperties = MaterialProperties()
        private var tier: ITier? = null
        private var colors: IntArray? = null

        fun tier(tier: Int) = apply { this.tier = ClayTiers.entries[tier] }
        fun tier(tier: ITier) = apply { this.tier = tier }
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

        /**
         * Adds a plate item to this material.
         * If the material has an ingot, dust, or block property, the plate recipe will be generated with the given parameters.
         */
        fun plate(cePerTick: ClayEnergy, requiredTick: Int, tier: Int): Builder {
            properties.setProperty(PropertyKey.PLATE, MaterialProperty.Plate(cePerTick, requiredTick, tier))
            return this
        }

        /**
         * Adds a clay block to this material.
         * @param decompressedInto If specified, the compress/condense and inverse recipe will be generated.
         * @param energy The energy of this clay. If null, the clay will not be energized (i.e. it can't be used as machine fuel).
         */
        fun clay(decompressedInto: Material? = null, energy: ClayEnergy? = null): Builder {
            properties.setProperty(PropertyKey.CLAY, Clay(decompressedInto, energy))
            return this
        }

        fun build(): Material{
            val material = Material(metaItemSubId, metaItemId, properties, tier, colors)
            ClayiumApi.materialRegistry.register(metaItemSubId, metaItemId, material)
            return material
        }
    }

    companion object {
        inline fun create(metaItemSubId: Int, metaItemId: ResourceLocation, init: Builder.() -> Unit): Material {
            return Builder(metaItemSubId, metaItemId).apply(init).build()
        }
    }
}