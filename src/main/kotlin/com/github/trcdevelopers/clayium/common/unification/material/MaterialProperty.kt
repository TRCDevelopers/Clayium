package com.github.trcdevelopers.clayium.common.unification.material

import net.minecraft.item.ItemStack

sealed interface MaterialProperty {
    fun verify(material: Material): Boolean

    sealed class IndependentProperty : MaterialProperty {
        override fun verify(material: Material) = true
    }

    data object Ingot : IndependentProperty()
    data object Dust : IndependentProperty()
    data object Matter : IndependentProperty()

    class Plate(
        val recipeTime: Int,
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
        fun getColorForItemStack(i: Int, stack: ItemStack) = colors[i]
    }
}
