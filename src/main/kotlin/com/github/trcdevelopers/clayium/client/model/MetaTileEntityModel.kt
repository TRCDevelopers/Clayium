package com.github.trcdevelopers.clayium.client.model

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
    override fun bake(
        state: IModelState,
        format: VertexFormat,
        bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>
    ): IBakedModel {
        if (!ModelTextures.isInitialized) ModelTextures.initialize(bakedTextureGetter)
        return MetaTileEntityBakedModel()
    }
}