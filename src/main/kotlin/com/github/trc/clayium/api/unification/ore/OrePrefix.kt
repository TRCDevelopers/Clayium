package com.github.trc.clayium.api.unification.ore

import com.github.trc.clayium.api.FALLBACK
import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.unification.material.CMarkerMaterials
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CMaterialFlags
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.api.unification.material.IMaterial
import com.github.trc.clayium.api.unification.material.MaterialAmount
import com.github.trc.clayium.common.util.BothSideI18n
import com.google.common.base.CaseFormat
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import java.util.function.Predicate

private val Long.M get() = MaterialAmount.of(this)

class OrePrefix(
    val camel: String,
    private val _materialAmount: MaterialAmount,
    val itemGenerationLogic: Predicate<CMaterial>? = null,
) {
    init {
        _prefixes.add(this)
    }

    val snake = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel)

    private val ignoredMaterials = mutableSetOf<CMaterial>()
    private val modifiedAmounts = Object2LongOpenHashMap<IMaterial>().apply { defaultReturnValue(FALLBACK.toLong()) }

    fun getMaterialAmount(material: IMaterial): MaterialAmount {
        val modified = modifiedAmounts.getLong(material)
        return if (modified != FALLBACK.toLong()) {
            MaterialAmount.createRaw(modified)
        } else {
            _materialAmount
        }
    }

    fun modifyAmount(material: IMaterial, amount: MaterialAmount) {
        modifiedAmounts.put(material, amount.raw)
    }

    //todo: move to somewhere else?
    fun canGenerateItem(material: CMaterial): Boolean {
        return !this.isIgnored(material) && (itemGenerationLogic != null
                && itemGenerationLogic.test(material))
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
        return BothSideI18n.format("${MOD_ID}.ore_prefix.${snake}", BothSideI18n.format(material.translationKey))
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

        val ingot = OrePrefix("ingot", 1.M, hasIngotProperty)
        val dust = OrePrefix("dust", 1.M, hasDustProperty)
        val gem = OrePrefix("gem", 1.M, hasMatterProperty)
        val crystal = OrePrefix("crystal", 1.M)
        val item = OrePrefix("item", 1.M)

        val plate = OrePrefix("plate", 1.M, hasPlateProperty)
        val largePlate = OrePrefix("largePlate", 4.M, hasPlateProperty)

        val impureDust = OrePrefix("impureDust", 1.M, hasImpureDustProperty)

        val block = OrePrefix("block", 9.M)

        //todo proper amount
        val bearing = OrePrefix("bearing", 1.M, hasClayPartsFlag)
        val blade = OrePrefix("blade", 1.M, hasClayPartsFlag)
        val cuttingHead = OrePrefix("cuttingHead", 9.M, hasClayPartsFlag)
        val cylinder = OrePrefix("cylinder", 2.M, hasClayPartsFlag)
        val disc = OrePrefix("disc", 1.M, hasClayPartsFlag)
        val gear = OrePrefix("gear", 1.M, hasClayPartsFlag)
        val grindingHead = OrePrefix("grindingHead", 16.M, hasClayPartsFlag)
        val needle = OrePrefix("needle", 2.M, hasClayPartsFlag)
        val pipe = OrePrefix("pipe", 1.M, hasClayPartsFlag)
        val ring = OrePrefix("ring", 1.M, hasClayPartsFlag)
        val shortStick = OrePrefix("shortStick", MaterialAmount.NONE, hasClayPartsFlag)
        val smallDisc = OrePrefix("smallDisc", MaterialAmount.NONE, hasClayPartsFlag)
        val smallRing = OrePrefix("smallRing", MaterialAmount.NONE, hasClayPartsFlag)
        val spindle = OrePrefix("spindle", 4.M, hasClayPartsFlag)
        val stick = OrePrefix("stick", MaterialAmount.NONE, hasClayPartsFlag)
        val wheel = OrePrefix("wheel", MaterialAmount.NONE, hasClayPartsFlag)

        val metaItemPrefixes = listOf(ingot, dust, impureDust, gem, plate, largePlate,
            bearing, blade, cuttingHead, cylinder, disc, gear, grindingHead, needle,
            pipe, ring, shortStick, smallDisc, smallRing, spindle, stick, wheel)

        fun init() {
            block.ignore(CMaterials.clay)

            listOf(CMaterials.pureAntimatter1, CMaterials.pureAntimatter2, CMaterials.pureAntimatter3,
                CMaterials.pureAntimatter4, CMaterials.pureAntimatter5, CMaterials.pureAntimatter6,
                CMaterials.pureAntimatter7
            ).forEach {
                block.ignore(it)
            }

            // silicone has 16 colored deco blocks, so disable auto-gen
            block.ignore(CMaterials.silicone)

            block.modifyAmount(CMarkerMaterials.certusQuartz, 4.M)
            block.modifyAmount(CMarkerMaterials.fluix, 4.M)
        }
    }
}