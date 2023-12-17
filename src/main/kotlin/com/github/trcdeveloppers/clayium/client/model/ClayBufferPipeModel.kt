package com.github.trcdeveloppers.clayium.client.model

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.property.IExtendedBlockState
import org.lwjgl.util.vector.Vector3f
import java.util.function.Function

class ClayBufferPipeModel(
    private val tier: Int,
) : IModel {

    override fun getTextures(): Collection<ResourceLocation> {
        return listOf(ResourceLocation("clayium:blocks/machinehull-${tier-1}"),)
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
        tier: Int,
    ) : IBakedModel {

        private val machineHull = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/machinehull-${tier-1}"))
        private val baseQuad = faceBakery.makeBakedQuad(
            Vector3f(6f, 6f, 6f), Vector3f(11f, 11f, 11f),
            BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(6f, 6f, 11f, 11f), 0)),
            this.machineHull,
            EnumFacing.NORTH, ModelRotation.X0_Y0,
            null, true, true
        )

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (state == null || side != null) return emptyList()

            val quads = mutableListOf<BakedQuad>()

            quads.add(baseQuad)

            return quads
        }

        override fun isAmbientOcclusion() = true
        override fun isGui3d() = true
        override fun isBuiltInRenderer() = false
        override fun getParticleTexture() = machineHull
        override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE

        private fun getFaceQuads(face: EnumFacing, state: IExtendedBlockState, rand: Long): List<BakedQuad> {
            face.rotateYCCW()
            return emptyList()
        }

        companion object {
            private val faceBakery = FaceBakery()
        }
    }
}