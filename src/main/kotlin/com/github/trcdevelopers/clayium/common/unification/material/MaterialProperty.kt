package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import net.minecraft.client.renderer.block.model.ModelResourceLocation

sealed interface MaterialProperty {
    fun verify(material: EnumMaterial): Boolean = true

    data object Ingot : MaterialProperty
    data object Dust : MaterialProperty
    class Matter(
        texture: String = "matter",
    ) : MaterialProperty {
        val modelLocation = ModelResourceLocation("${Clayium.MOD_ID}:colored/$texture", "inventory")
    }

    class Plate(
        val cePerTick: ClayEnergy,
        val requiredTick: Int,
        val tier: Int,
    ) : MaterialProperty {
        override fun verify(material: EnumMaterial) = material.hasProperty<Ingot>() || material.hasProperty<Matter>()
    }

    class ImpureDust(
        private vararg val colors: Int,
    ) : MaterialProperty {

        init {
            require(colors.size == 3) { "ImpureDust must have 3 color layers" }
        }

        override fun verify(material: EnumMaterial) = material.hasProperty<Dust>()
        fun getColor(i: Int) = colors[i]
    }
}

class Clay(val compressedInto: Material?) : MaterialProperty
class EnergyClay(val compressedInto: Material?, val energy: ClayEnergy) : MaterialProperty
