package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.util.getItem
import com.github.trcdevelopers.clayium.client.model.MaterialStateMapper
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.properties.CMaterialProperty
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.block.SoundType
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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

    @SideOnly(Side.CLIENT)
    fun registerModels() {
        ModelLoader.setCustomStateMapper(this, MaterialStateMapper)
        for (state in blockState.validStates) {
            ModelLoader.setCustomModelResourceLocation(
                this.getItem(), this.getMetaFromState(state), MaterialStateMapper.createModelLocation(state)
            )
        }
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