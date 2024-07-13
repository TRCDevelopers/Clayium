package com.github.trc.clayium.common.blocks.material

import com.github.trc.clayium.common.Clayium
import com.github.trc.clayium.common.blocks.BlockMaterialBase
import com.github.trc.clayium.common.blocks.properties.CMaterialProperty
import com.github.trc.clayium.common.unification.material.Material
import net.minecraft.block.SoundType

abstract class BlockCompressedClay(
    mapping: Map<Int, Material>
) : BlockMaterialBase(net.minecraft.block.material.Material.CLAY, mapping) {
    init {
        setSoundType(SoundType.GROUND)
        setHarvestLevel("shovel", 0)
        setHardness(0.6f)

        setTranslationKey("compressed_clay")
        setCreativeTab(Clayium.creativeTab)
    }

    companion object {
        fun create(mapping: Map<Int, Material>): BlockCompressedClay {
            val materials = mapping.values
            val prop = CMaterialProperty(materials, "material")
            return object : BlockCompressedClay(mapping) {
                override fun getMaterialProperty() = prop
            }
        }
    }
}