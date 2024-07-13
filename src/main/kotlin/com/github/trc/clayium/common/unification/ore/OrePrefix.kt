package com.github.trc.clayium.common.unification.ore

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.material.Material
import com.github.trc.clayium.common.unification.material.MaterialFlags
import com.github.trc.clayium.common.unification.material.PropertyKey
import com.google.common.base.CaseFormat
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Predicate

class OrePrefix(
    val camel: String,
    val itemGenerationLogic: Predicate<Material>? = null,
) {
    init {
        _prefixes.add(this)
    }

    val snake = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel)

    private val ignoredMaterials = mutableSetOf<Material>()

    //todo: move to somewhere else?
    fun canGenerateItem(material: Material): Boolean {
        return !this.isIgnored(material) && (itemGenerationLogic == null
                || itemGenerationLogic.test(material))
    }

    fun ignore(material: Material) {
        ignoredMaterials.add(material)
    }

    fun isIgnored(material: Material): Boolean {
        return ignoredMaterials.contains(material)
    }

    @SideOnly(Side.CLIENT)
    fun getLocalizedName(material: Material): String {
        val specialKey = "${material.translationKey}.${this.snake}"
        if (I18n.hasKey(specialKey)) return I18n.format(specialKey)
        return I18n.format("${CValues.MOD_ID}.ore_prefix.${snake}", material.localizedName)
    }

    override fun toString(): String {
        return "OrePrefix(camel=$camel, snake=$snake)"
    }

    companion object {
        private val _prefixes = mutableListOf<OrePrefix>()
        val allPrefixes: List<OrePrefix> = _prefixes

        private val hasIngotProperty = Predicate<Material> { it.hasProperty(PropertyKey.INGOT) }
        private val hasDustProperty = Predicate<Material> { it.hasProperty(PropertyKey.DUST) }
        private val hasPlateProperty = Predicate<Material> { it.hasProperty(PropertyKey.PLATE) }
        private val hasMatterProperty = Predicate<Material> { it.hasProperty(PropertyKey.MATTER) }
        private val hasImpureDustProperty = Predicate<Material> { it.hasProperty(PropertyKey.IMPURE_DUST) }
        private val hasClayPartsFlag = Predicate<Material> { it.hasFlag(MaterialFlags.GENERATE_CLAY_PARTS) }

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
        }
    }
}