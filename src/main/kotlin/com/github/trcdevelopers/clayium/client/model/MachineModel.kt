package com.github.trcdevelopers.clayium.client.model

import com.google.common.collect.ImmutableList
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import java.util.function.Function

class MachineModel(
    private val facing: EnumFacing,
    private val machineHullTexture: ResourceLocation
) : IModel {

    override fun getTextures(): Collection<ResourceLocation> {
        return ImmutableList.of(
            machineHullTexture,
            ResourceLocation("clayium:blocks/import_energy"),
            ResourceLocation("clayium:blocks/import"),
            ResourceLocation("clayium:blocks/import_1"),
            ResourceLocation("clayium:blocks/import_2"),
            ResourceLocation("clayium:blocks/import_12"),
            ResourceLocation("clayium:blocks/export"),
            ResourceLocation("clayium:blocks/export_1"),
            ResourceLocation("clayium:blocks/export_2"),
            ResourceLocation("clayium:blocks/export_12"),
        )
    }

    override fun bake(
        state: IModelState,
        format: VertexFormat,
        bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>
    ): IBakedModel {
        return MachineBakedModel(bakedTextureGetter, machineHullTexture)
    }
}