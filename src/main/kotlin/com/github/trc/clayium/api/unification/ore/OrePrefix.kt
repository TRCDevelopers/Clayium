package com.github.trc.clayium.api.unification.ore

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CMaterialFlags
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.common.util.BothSideI18n
import com.google.common.base.CaseFormat
import java.util.function.Predicate

class OrePrefix(
    val camel: String,
    val itemGenerationLogic: Predicate<CMaterial>? = null,
) {
    init {
        _prefixes.add(this)
    }

    val snake = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel)

    private val ignoredMaterials = mutableSetOf<CMaterial>()

    //todo: move to somewhere else?
    fun canGenerateItem(material: CMaterial): Boolean {
        return !this.isIgnored(material) && (itemGenerationLogic == null
                || itemGenerationLogic.test(material))
    }

    fun ignore(material: CMaterial) {
        ignoredMaterials.add(material)
    }

    fun isIgnored(material: CMaterial): Boolean {
        return ignoredMaterials.contains(material)
    }

    fun getLocalizedName(material: CMaterial): String {
        val specialKey = "${material.translationKey}.${this.snake}"
        if (BothSideI18n.hasKey(specialKey)) {
            return BothSideI18n.format(specialKey)
        }
        return BothSideI18n.format("${CValues.MOD_ID}.ore_prefix.${snake}", BothSideI18n.format(material.translationKey))
    }

    override fun toString(): String {
        return "OrePrefix(camel=$camel, snake=$snake)"
    }

    companion object {
        private val _prefixes = mutableListOf<OrePrefix>()
        val allPrefixes: List<OrePrefix> = _prefixes

        private val hasIngotProperty = Predicate<CMaterial> { it.hasProperty(CPropertyKey.Companion.INGOT) }
        private val hasDustProperty = Predicate<CMaterial> { it.hasProperty(CPropertyKey.Companion.DUST) }
        private val hasPlateProperty = Predicate<CMaterial> { it.hasProperty(CPropertyKey.Companion.PLATE) }
        private val hasMatterProperty = Predicate<CMaterial> { it.hasProperty(CPropertyKey.Companion.MATTER) }
        private val hasImpureDustProperty = Predicate<CMaterial> { it.hasProperty(CPropertyKey.Companion.IMPURE_DUST) }
        private val hasClayPartsFlag = Predicate<CMaterial> { it.hasFlag(CMaterialFlags.GENERATE_CLAY_PARTS) }

        val ingot = OrePrefix("ingot", hasIngotProperty)
        val dust = OrePrefix("dust", hasDustProperty)
        val gem = OrePrefix("gem", hasMatterProperty)
        val plate = OrePrefix("plate", hasPlateProperty)
        val largePlate = OrePrefix("largePlate", hasPlateProperty)

        val impureDust = OrePrefix("impureDust", hasImpureDustProperty)

        val block = OrePrefix("block")

        val bearing = OrePrefix("bearing", hasClayPartsFlag)
        val blade = OrePrefix("blade", hasClayPartsFlag)
        val cuttingHead = OrePrefix("cuttingHead", hasClayPartsFlag)
        val cylinder = OrePrefix("cylinder", hasClayPartsFlag)
        val disc = OrePrefix("disc", hasClayPartsFlag)
        val gear = OrePrefix("gear", hasClayPartsFlag)
        val grindingHead = OrePrefix("grindingHead", hasClayPartsFlag)
        val needle = OrePrefix("needle", hasClayPartsFlag)
        val pipe = OrePrefix("pipe", hasClayPartsFlag)
        val ring = OrePrefix("ring", hasClayPartsFlag)
        val shortStick = OrePrefix("shortStick", hasClayPartsFlag)
        val smallDisc = OrePrefix("smallDisc", hasClayPartsFlag)
        val smallRing = OrePrefix("smallRing", hasClayPartsFlag)
        val spindle = OrePrefix("spindle", hasClayPartsFlag)
        val stick = OrePrefix("stick", hasClayPartsFlag)
        val wheel = OrePrefix("wheel", hasClayPartsFlag)

        val metaItemPrefixes = listOf(ingot, dust, impureDust, gem, plate, largePlate,
            bearing, blade, cuttingHead, cylinder, disc, gear, grindingHead, needle,
            pipe, ring, shortStick, smallDisc, smallRing, spindle, stick, wheel)

        fun init() {
            block.ignore(CMaterials.clay)

            ingot.ignore(CMaterials.iron)

            dust.ignore(CMaterials.iron)
        }
    }
}