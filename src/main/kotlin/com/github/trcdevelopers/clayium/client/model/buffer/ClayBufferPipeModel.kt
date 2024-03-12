package com.github.trcdevelopers.clayium.client.model.buffer

import com.github.trcdevelopers.clayium.client.ModelUtils
import com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer.BlockClayBuffer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.function.Function

class ClayBufferPipeModel(
    private val tier: Int,
) : IModel {

    override fun getTextures(): Collection<ResourceLocation> {
        return listOf(
            ResourceLocation("clayium:blocks/machinehull_tier$tier"),
            ResourceLocation("clayium:blocks/import"),
            ResourceLocation("clayium:blocks/export"),
        )
    }

    override fun bake(
        state: IModelState,
        format: VertexFormat,
        bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>
    ): IBakedModel {
        return ClayBufferPipeBakedModel(bakedTextureGetter, tier)
    }

    private class ClayBufferPipeBakedModel(
        bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>,
        machineHullTier: Int,
    ) : IBakedModel {

        private val machineHull = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/machinehull_tier$machineHullTier"))

        private val sideCubeQuads = ModelUtils.createSideCubeQuads(machineHull)
        private val centerCubeQuad = ModelUtils.createCenterCubeQuads(machineHull)

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (state == null || side != null) return emptyList()

            val connections = (state as IExtendedBlockState).getValue(BlockClayBuffer.CONNECTIONS)
            val quads = mutableListOf<BakedQuad>()

            for (index in EnumFacing.entries.indices) {
                if (connections[index]) {
                    quads.addAll(sideCubeQuads[index])
                } else {
                    quads.add(centerCubeQuad[index])
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
}