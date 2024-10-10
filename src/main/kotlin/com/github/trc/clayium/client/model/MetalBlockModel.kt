package com.github.trc.clayium.client.model

import codechicken.lib.render.particle.IModelParticleProvider
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
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.function.Function

class MetalBlockModel : IModel {
    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        return MetalBlockBakedModel(bakedTextureGetter)
    }

    class MetalBlockBakedModel(
        private val texGetter: Function<ResourceLocation, TextureAtlasSprite>
    ) : IModelParticleProvider {

        // MaterialName -> BakedQuads
        private val cache = mutableMapOf<String, List<BakedQuad>>()

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (state == null || side == null) return emptyList()
            val exState = state as? IExtendedBlockState ?: return emptyList()
            val materialName = exState.getValue(BlockCompressed.MATERIAL_NAME)
            val quads = mutableListOf<BakedQuad>()
            val allSideQuads = cache.getOrPut(materialName) {
                val atlas = texGetter.apply(clayiumId("blocks/compressed_$materialName"))
                EnumFacing.entries.map { ModelTextures.createQuad(it, atlas) }
            }
            quads.add(allSideQuads[side.index])
            return quads
        }

        override fun getHitEffects(traceResult: RayTraceResult, state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite?> {
            return getParticle(state)
        }

        override fun getDestroyEffects(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite?> {
            return getParticle(state)
        }

        private fun getParticle(state: IBlockState?): Set<TextureAtlasSprite> {
            val state = state as? IExtendedBlockState ?: return emptySet()
            val materialName = state.getValue(BlockCompressed.MATERIAL_NAME)
            val atlas = texGetter.apply(clayiumId("blocks/compressed_$materialName"))
            return setOf(atlas)
        }

        override fun isAmbientOcclusion() = true
        override fun isGui3d() = true
        override fun isBuiltInRenderer() = false

        override fun getOverrides(): ItemOverrideList {
            return ItemOverrideList.NONE
        }
    }
}