package com.github.trc.clayium.api.unification.material

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.Clayium
import com.google.common.base.CaseFormat
import net.minecraft.util.ResourceLocation

class CMaterial(
    val metaItemSubId: Int,
    /**
     * modid:material_name
     */
    val materialId: ResourceLocation,
    val properties: CMaterialProperties,
    override val blockAmount: Int,
    override val tier: ITier? = null,
    val colors: IntArray? = null,
    private val flags: Set<CMaterialFlag> = emptySet(),
) : Comparable<CMaterial>, IMaterial {

    override val upperCamelName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, materialId.path)
    val translationKey = "${materialId.namespace}.material.${materialId.path}"

    override fun compareTo(other: CMaterial): Int {
        return metaItemSubId.compareTo(other.metaItemSubId)
    }

    fun hasFlag(flag: CMaterialFlag) = flags.contains(flag)
    fun hasProperty(key: CPropertyKey<*>) = properties.hasProperty(key)
    fun <T : MaterialProperty> getProperty(key: CPropertyKey<T>) = properties.getProperty(key)
    fun <T : MaterialProperty> getPropOrNull(key: CPropertyKey<T>) = properties.getPropOrNull(key)

    override fun toString(): String {
        return "Material(metaItemSubId=$metaItemSubId, materialId=$materialId, properties=$properties, tier=$tier, colors=${colors?.contentToString()}, flags=$flags)"
    }

    class Builder(
        private val metaItemSubId: Int,
        private val metaItemId: ResourceLocation,
    ) {
        private var properties: CMaterialProperties = CMaterialProperties()
        private var tier: ITier? = null
        private var colors: IntArray? = null
        private var flags: MutableSet<CMaterialFlag> = mutableSetOf()
        private var blockAmount = 9

        fun tier(tier: Int) = apply { this.tier = ClayTiers.entries[tier] }
        fun tier(tier: ITier) = apply { this.tier = tier }
        fun colors(vararg colors: Int): Builder {
            if (colors.isEmpty()) {
                Clayium.LOGGER.warn("Material.Builder#colors is called, but provided array is empty. Ignoring.")
                return this
            }
            this.colors = colors
            return this
        }

        fun blockAmount(amount: Int) = apply { blockAmount = amount }

        fun ingot() = apply { properties.setProperty(CPropertyKey.INGOT, MaterialProperty.Ingot) }
        fun dust() = apply { properties.setProperty(CPropertyKey.DUST, MaterialProperty.Dust) }

        fun matter(texture: String = "matter"): Builder {
            properties.setProperty(CPropertyKey.MATTER, MaterialProperty.Matter(texture))
            return this
        }

        fun impureDust(color1: Int, color2: Int, color3: Int): Builder {
            properties.setProperty(CPropertyKey.IMPURE_DUST, MaterialProperty.ImpureDust(color1, color2, color3))
            return this
        }

        /**
         * Adds a plate item to this material.
         * If the material has an ingot, dust, or block property, the plate recipe will be generated with the given parameters.
         */
        fun plate(cePerTick: ClayEnergy, requiredTick: Int, tier: Int): Builder {
            properties.setProperty(CPropertyKey.PLATE, MaterialProperty.Plate(cePerTick, requiredTick, tier))
            return this
        }

        /**
         * Adds a clay block to this material.
         * automatically set blockAmount to 1.
         * @param compressedInto If specified, the compress/condense and inverse recipe will be generated.
         * @param energy The energy of this clay. If null, the clay will not be energized (i.e. it can't be used as machine fuel).
         */
        fun clay(compressionLevel: Int, compressedInto: CMaterial? = null, energy: ClayEnergy? = null): Builder {
            properties.setProperty(CPropertyKey.CLAY, Clay(compressionLevel, compressedInto, energy))
            blockAmount(1)
            return this
        }

        /**
         * Adds a clay smelting recipe and removes a vanilla smelting recipe.
         */
        fun claySmelting(tier: Int, duration: Int): Builder {
            properties.setProperty(CPropertyKey.CLAY_SMELTING, ClaySmelting(tier, duration))
            return this
        }

        /**
         * Adds a clay smelting recipe and removes a vanilla smelting recipe.
         */
        fun claySmelting(factor: Double, tier: Int, duration: Int): Builder {
            properties.setProperty(CPropertyKey.CLAY_SMELTING, ClaySmelting(factor, tier, duration))
            return this
        }

        /**
         * Adds a blast smelting recipe and removes a vanilla smelting recipe.
         */
        fun blastSmelting(tier: Int, duration: Int): Builder {
            properties.setProperty(CPropertyKey.BLAST_SMELTING, BlastSmelting(tier, duration))
            return this
        }

        /**
         * Adds a blast smelting recipe and removes a vanilla smelting recipe.
         */
        fun blastSmelting(factor: Double, tier: Int, duration: Int): Builder {
            properties.setProperty(CPropertyKey.BLAST_SMELTING, BlastSmelting(factor, tier, duration))
            return this
        }

        fun flags(vararg flags: CMaterialFlag): Builder {
            this.flags.addAll(flags)
            return this
        }

        fun build(): CMaterial{
            val flags = if (this.flags.isEmpty()) emptySet() else this.flags
            val material = CMaterial(metaItemSubId, metaItemId, properties, blockAmount, tier, colors, flags)
            ClayiumApi.materialRegistry.register(metaItemSubId, metaItemId, material)
            return material
        }
    }

    companion object {
        inline fun create(metaItemSubId: Int, metaItemId: ResourceLocation, init: Builder.() -> Unit): CMaterial {
            return Builder(metaItemSubId, metaItemId).apply(init).build()
        }
    }
}