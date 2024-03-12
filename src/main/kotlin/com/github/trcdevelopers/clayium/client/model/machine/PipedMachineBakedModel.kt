package com.github.trcdevelopers.clayium.client.model.machine

import com.github.trcdevelopers.clayium.client.ModelUtils
import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.function.Function

class PipedMachineBakedModel(
    machineHullLocation: ResourceLocation,
    bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>,
) : IBakedModel {

    private val machineHull = bakedTextureGetter.apply(machineHullLocation)

    private val sideCubeQuads = ModelUtils.createSideCubeQuads(machineHull)
    private val centerCubeQuad = ModelUtils.createCenterCubeQuads(machineHull)

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (state == null || side != null) return emptyList()

        val connections = (state as IExtendedBlockState).getValue(BlockMachine.CONNECTIONS)
        val quads = mutableListOf<BakedQuad>()

        for (i in EnumFacing.entries.indices) {
            if (connections[i]) {
                quads.addAll(sideCubeQuads[i])
            } else {
                quads.add(centerCubeQuad[i])
            }
        }
        return quads
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
    override fun isBuiltInRenderer() = false
    override fun getParticleTexture() = machineHull
    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE
}