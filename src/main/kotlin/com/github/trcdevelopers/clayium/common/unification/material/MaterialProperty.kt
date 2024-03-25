package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import net.minecraft.client.renderer.block.model.ModelResourceLocation

sealed interface MaterialProperty {
    fun verify(material: Material): Boolean

    sealed class IndependentProperty : MaterialProperty {
        override fun verify(material: Material) = true
    }

    data object Ingot : IndependentProperty()
    data object Dust : IndependentProperty()
    class Matter(
        texture: String = "matter",
    ) : IndependentProperty() {
        val modelLocation = ModelResourceLocation("${Clayium.MOD_ID}:colored/$texture", "inventory")
    }

    class Plate(
        val cePerTick: ClayEnergy,
        val requiredTick: Int,
        val tier: Int,
    ) : MaterialProperty {
        override fun verify(material: Material) = material.hasProperty<Ingot>() || material.hasProperty<Matter>()
    }

    class ImpureDust(
        private vararg val colors: Int,
    ) : MaterialProperty {

        init {
            require(colors.size == 3) { "ImpureDust must have 3 color layers" }
        }

        override fun verify(material: Material) = material.hasProperty<Dust>()
        fun getColor(i: Int) = colors[i]
    }
}
