package com.github.trcdevelopers.clayium.client.model.machine

import com.github.trcdevelopers.clayium.client.ModelUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import java.util.function.Function

class PipedMachineBakedModel(
    machineHullLocation: ResourceLocation,
    bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>,
) : IBakedModel {

    private val machineHull = bakedTextureGetter.apply(machineHullLocation)

    private val sideCubeQuads = ModelUtils.createSideCubeQuads(machineHull)
    private val centerCubeQuad = ModelUtils.createCenterCubeQuads(machineHull)

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        TODO("Not yet implemented")
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
    override fun isBuiltInRenderer() = false
    override fun getParticleTexture() = machineHull
    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE
}