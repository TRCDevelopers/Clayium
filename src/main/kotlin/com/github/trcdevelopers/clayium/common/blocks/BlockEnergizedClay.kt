package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.blocks.properties.CMaterialProperty
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.block.SoundType

abstract class BlockEnergizedClay : BlockMaterialBase(net.minecraft.block.material.Material.GROUND) {
    init {
        setSoundType(SoundType.GROUND)
        setHarvestLevel("shovel", 0)
        setHardness(0.6f)
    }

    companion object {
        fun create(materials: Collection<Material>): BlockEnergizedClay {
            val prop = CMaterialProperty(materials, "material")
            return object : BlockEnergizedClay() {
                override fun getMaterialProperty() = prop
            }
        }
    }
}