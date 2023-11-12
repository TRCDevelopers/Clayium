package com.github.trcdeveloppers.clayium.client.model

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import java.util.function.Function

class CeContainerModel(
    private val machineHullTier: Int = 4,
) : IModel {

    override fun getTextures(): MutableCollection<ResourceLocation> {
        return mutableListOf(
            ResourceLocation("clayium:blocks/machinehull-$machineHullTier"),
            ResourceLocation("clayium:blocks/import"),
            ResourceLocation("clayium:blocks/import_energy"),
            ResourceLocation("clayium:blocks/export"),
        )
    }

    override fun bake(
        state: IModelState,
        format: VertexFormat,
        bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>
    ): IBakedModel {
        return CeContainerBakedModel(bakedTextureGetter, machineHullTier)
    }
}