package io.github.trcdevelopers.clayium.client.model

import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.blocks.material.BlockMaterialWithDynModel
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.function.Function

object MetalBlockModel : IModel {

    private var bakedModel: MetalBlockBakedModel? = null

    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        return bakedModel ?: MetalBlockBakedModel(bakedTextureGetter).also { bakedModel = it }
    }

    class MetalBlockBakedModel(
        private val texGetter: Function<ResourceLocation, TextureAtlasSprite>
    ) : MaterialBlockBakedModel(texGetter) {

        // MaterialName -> BakedQuads
        private val cache = mutableMapOf<String, List<BakedQuad>>()

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (state == null || side == null) return emptyList()
            val exState = state as? IExtendedBlockState ?: return emptyList()
            val materialName = exState.getValue(BlockMaterialWithDynModel.MATERIAL_NAME)
            val quads = mutableListOf<BakedQuad>()
            val allSideQuads = cache.getOrPut(materialName) {
                val atlas = texGetter.apply(clayiumId("blocks/compressed_$materialName"))
                EnumFacing.entries.map { ModelTextures.createQuad(it, atlas) }
            }
            quads.add(allSideQuads[side.index])
            return quads
        }
    }
}