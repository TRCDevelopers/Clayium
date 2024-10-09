package com.github.trc.clayium.client.model

import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.blocks.material.BlockCompressed
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.function.Function

class MetalBlockModel : IModel {
    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        return MetalBlockBakedModel(bakedTextureGetter)
    }

    class MetalBlockBakedModel(
        private val texGetter: Function<ResourceLocation, TextureAtlasSprite>
    ) : IBakedModel {

        // MaterialName -> BakedQuads
        private val cache = mutableMapOf<String, List<BakedQuad>>()

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (state == null || side == null) return emptyList()
            val exState = state as? IExtendedBlockState ?: return emptyList()
            val materialName = exState.getValue(BlockCompressed.MATERIAL_NAME)
            return cache.getOrPut(materialName) {
                val atlas = texGetter.apply(clayiumId("blocks/compressed_$materialName"))
                EnumFacing.entries.map { ModelTextures.createQuad(it, atlas) }
            }
        }

        override fun isAmbientOcclusion() = true
        override fun isGui3d() = true
        override fun isBuiltInRenderer() = false
        override fun getParticleTexture(): TextureAtlasSprite {
            return ModelLoader.White.INSTANCE
        }

        override fun getOverrides(): ItemOverrideList {
            return ItemOverrideList.NONE
        }
    }
}