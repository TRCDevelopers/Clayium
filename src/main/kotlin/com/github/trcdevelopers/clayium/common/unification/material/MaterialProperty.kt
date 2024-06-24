package com.github.trcdevelopers.clayium.common.unification.material

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries

sealed interface MaterialProperty {
    fun verify(material: EnumMaterial): Boolean

    sealed class IndependentProperty : MaterialProperty {
        override fun verify(material: EnumMaterial) = true
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

/**
 * Adapter for block - material.
 * Assumes that the given (blockId, meta) pair exists as an ItemBlock and is associated with the material.
 */
class Block(val blockId: ResourceLocation, val meta: Int) : MaterialProperty {
    override fun verify(material: EnumMaterial) = true
    fun getStackForm(count: Int = 1): ItemStack {
        return ItemStack(ForgeRegistries.BLOCKS.getValue(blockId)!!, count)
    }
}