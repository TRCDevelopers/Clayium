package com.github.trcdevelopers.clayium.common.unification.ore

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey
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

    companion object {
        private val _prefixes = mutableListOf<OrePrefix>()
        val allPrefixes: List<OrePrefix> = _prefixes

        private val hasIngotProperty = Predicate<Material> { it.hasProperty(PropertyKey.INGOT) }
        private val hasDustProperty = Predicate<Material> { it.hasProperty(PropertyKey.DUST) }
        private val hasPlateProperty = Predicate<Material> { it.hasProperty(PropertyKey.PLATE) }
        private val hasMatterProperty = Predicate<Material> { it.hasProperty(PropertyKey.MATTER) }
        private val hasImpureDustProperty = Predicate<Material> { it.hasProperty(PropertyKey.IMPURE_DUST) }

        val ingot = OrePrefix("ingot", hasIngotProperty)
        val dust = OrePrefix("dust", hasDustProperty)
        val plate = OrePrefix("plate", hasPlateProperty)
        val largePlate = OrePrefix("largePlate", hasPlateProperty)
        val matter = OrePrefix("matter", hasMatterProperty)

        val impureDust = OrePrefix("impureDust", hasImpureDustProperty)

        val block = OrePrefix("block")

        val metaItemPrefixes = listOf(ingot, dust, impureDust, matter, plate, largePlate)

        fun init() {
            block.ignore(CMaterials.clay)
        }
    }
}