package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey.Companion.DUST
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey.Companion.INGOT
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey.Companion.MATTER
import net.minecraft.client.renderer.block.model.ModelResourceLocation

sealed interface MaterialProperty {
    fun verify(material: Material): Boolean = true

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
        override fun verify(material: Material) = material.hasProperty(INGOT) || material.hasProperty(MATTER)
    }

    class ImpureDust(
        private vararg val colors: Int,
    ) : MaterialProperty {

        init {
            require(colors.size == 3) { "ImpureDust must have 3 color layers" }
        }

        override fun verify(material: Material) = material.hasProperty(DUST)
        fun getColor(i: Int) = colors[i]
    }
}

class Clay(val compressedInto: Material?, val energy: ClayEnergy?) : MaterialProperty
