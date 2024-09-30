package com.github.trc.clayium.common.blocks.material

import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.common.blocks.BlockMaterialBase
import com.github.trc.clayium.common.blocks.properties.CMaterialProperty
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.block.SoundType

abstract class BlockCompressedClay(
    mapping: Map<Int, CMaterial>
) : BlockMaterialBase(net.minecraft.block.material.Material.CLAY, mapping) {
    init {
        setSoundType(SoundType.GROUND)
        setHarvestLevel("shovel", 0)
        setHardness(0.6f)

        setTranslationKey("compressed_clay")
        setCreativeTab(ClayiumCTabs.main)
    }

    companion object {
        fun create(mapping: Map<Int, CMaterial>): BlockCompressedClay {
            val materials = mapping.values
            val prop = CMaterialProperty(materials, "material")
            return object : BlockCompressedClay(mapping) {
                override fun getMaterialProperty() = prop
            }
        }
    }
}