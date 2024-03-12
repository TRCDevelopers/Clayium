package com.github.trcdevelopers.clayium.client.model.buffer

import com.github.trcdevelopers.clayium.client.model.ClayiumBakedModel
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

class ClayBufferModel(
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
        bakedTextureGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>
    ): IBakedModel {
        return ClayBufferBakedModel(bakedTextureGetter, tier)
    }

    private class ClayBufferBakedModel(
        bakedTextureGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>,
        tier: Int,
    ) : ClayiumBakedModel() {

        private val machineHull = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/machinehull_tier$tier"))
        private val import = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/import"))
        private val export = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/export"))

        private val baseQuads = EnumFacing.entries.associateWith { side ->
            this.getFaceQuad(side, this.machineHull)
        }

        override fun isAmbientOcclusion() = true
        override fun isGui3d() = true
        override fun isBuiltInRenderer() = false
        override fun getParticleTexture() = machineHull
        override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (side == null || state == null) {
                return emptyList()
            }

            val extState = state as IExtendedBlockState
            val quads = mutableListOf<BakedQuad>()

            quads.add(this.baseQuads.getValue(side))

            // Import
            if (extState.getValue(BlockClayBuffer.INPUTS)[side.index]) {
                quads.add(this.getFaceQuad(side, this.import))
            }

            // Export
            if (extState.getValue(BlockClayBuffer.OUTPUTS)[side.index]) {
                quads.add(this.getFaceQuad(side, this.export))
            }

            return quads
        }
    }
}