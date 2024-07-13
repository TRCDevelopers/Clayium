package com.github.trc.clayium.client.model

import com.github.trc.clayium.common.blocks.BlockMaterialBase
import com.github.trc.clayium.common.blocks.properties.CMaterialProperty
import com.github.trc.clayium.common.unification.material.Material
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraftforge.client.model.ModelLoader

object MaterialStateMapper : IStateMapper {

    private val INDEX_PATTERN = Regex("_[0-9]+$")

    override fun putStateModelLocations(blockIn: Block): Map<IBlockState, ModelResourceLocation> {
        if (blockIn !is BlockMaterialBase) throw IllegalArgumentException("Block is not a BlockMaterialBase")
        return blockIn.blockState.validStates.associateWith(::createModelLocation)
    }

    fun createModelLocation(state: IBlockState): ModelResourceLocation {
        for ((property, value) in state.properties) {
            if (property !is CMaterialProperty || value !is Material) continue

            val name = property.getName(value)
            val split = name.split("__")
            if (split.size != 2) throw IllegalArgumentException("Invalid material name: $name")
            val modId = split[0]
            val materialName = split[1]
            val blockName = state.block.registryName!!.path.replace(INDEX_PATTERN, "")

            return ModelResourceLocation("$modId:material/$blockName", "material=${materialName}")
        }
        return ModelLoader.MODEL_MISSING
    }
}