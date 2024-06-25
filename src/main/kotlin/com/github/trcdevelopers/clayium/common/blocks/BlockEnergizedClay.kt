package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.properties.CMaterialProperty
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.block.SoundType

abstract class BlockEnergizedClay(
    mapping: Map<Int, Material>,
) : BlockMaterialBase(net.minecraft.block.material.Material.GROUND, mapping) {

    init {
        setSoundType(SoundType.GROUND)
        setHarvestLevel("shovel", 0)
        setHardness(0.6f)

        setTranslationKey("energized_clay")
        setCreativeTab(Clayium.creativeTab)
    }

    companion object {
        fun create(mapping: Map<Int, Material>): BlockEnergizedClay {
            val materials = mapping.values
            val prop = CMaterialProperty(materials, "material")
            return object : BlockEnergizedClay(mapping) {
                override fun getMaterialProperty() = prop
            }
        }
    }
}