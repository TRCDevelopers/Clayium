package io.github.trcdevelopers.clayium.common.blocks.material

import io.github.trcdevelopers.clayium.api.unification.material.CMaterial
import io.github.trcdevelopers.clayium.common.blocks.properties.CMaterialProperty
import io.github.trcdevelopers.clayium.common.creativetab.ClayiumCTabs
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