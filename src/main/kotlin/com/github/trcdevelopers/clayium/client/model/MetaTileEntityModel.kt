package com.github.trcdevelopers.clayium.client.model

import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import java.util.function.Function

class MetaTileEntityModel(
    private val isPipe: Boolean,
) : IModel {

    override fun getTextures(): Collection<ResourceLocation> {
        return listOf(
            // io textures
            clayiumId("blocks/import_energy"),
            clayiumId("blocks/import"),
            clayiumId("blocks/import_1"),
            clayiumId("blocks/import_2"),
            clayiumId("blocks/import_12"),
            clayiumId("blocks/export"),
            clayiumId("blocks/export_1"),
            clayiumId("blocks/export_2"),
            clayiumId("blocks/export_12"),
            // machine hulls
            clayiumId("blocks/machinehull_tier1"),
            clayiumId("blocks/machinehull_tier2"),
            clayiumId("blocks/machinehull_tier3"),
            clayiumId("blocks/machinehull_tier4"),
            clayiumId("blocks/machinehull_tier5"),
            clayiumId("blocks/machinehull_tier6"),
            clayiumId("blocks/machinehull_tier7"),
            clayiumId("blocks/machinehull_tier8"),
            clayiumId("blocks/machinehull_tier9"),
            clayiumId("blocks/machinehull_tier10"),
            clayiumId("blocks/machinehull_tier11"),
            clayiumId("blocks/machinehull_tier12"),
            clayiumId("blocks/machinehull_tier13"),
        )
    }

    override fun bake(
        state: IModelState,
        format: VertexFormat,
        bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>
    ): IBakedModel {
        if (!ModelTextures.isInitialized) ModelTextures.initialize(bakedTextureGetter)
        return if (isPipe) {
            MetaTileEntityPipeBakedModel()
        } else {
            MetaTileEntityBakedModel(bakedTextureGetter)
        }
    }
}