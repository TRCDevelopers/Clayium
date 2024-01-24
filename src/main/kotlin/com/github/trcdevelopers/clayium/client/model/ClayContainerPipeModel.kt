package com.github.trcdevelopers.clayium.client.model

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
import org.lwjgl.util.vector.Vector3f

class ClayContainerPipeModel(
    private val machineHullTier: Int,
) : IModel {

    override fun getTextures(): MutableCollection<ResourceLocation> {
        return mutableListOf(
            ResourceLocation("clayium:blocks/machinehull_tier$machineHullTier"),
        )
    }

    override fun bake(
        state: IModelState,
        format: VertexFormat,
        bakedTextureGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>
    ): IBakedModel {
        return ClayContainerPipeBakedModel(bakedTextureGetter, machineHullTier)
    }

    private class ClayContainerPipeBakedModel(
        bakedTextureGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>,
        tier: Int,
    ) : IBakedModel {

        private val machineHull = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/machinehull_$tier"))

        private val baseQuads = EnumFacing.entries.map {
            faceBakery.makeBakedQuad(
                Vector3f(4f, 4f, 4f),
                Vector3f(12f, 12f, 12f),
                BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(4f, 4f, 12f, 12f), 0)),
                this.machineHull,
                it,
                ModelRotation.X0_Y0,
                null,
                true,
                true
            )
        }


        override fun isAmbientOcclusion() = true
        override fun isGui3d() = true
        override fun isBuiltInRenderer() = false

        override fun getParticleTexture(): TextureAtlasSprite = this.machineHull
        override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (state == null || side != null) return emptyList()

            return baseQuads
        }

        companion object {
            private val faceBakery = FaceBakery()
        }
    }
}