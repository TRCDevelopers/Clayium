package com.github.trcdevelopers.clayium.client.model.machine

import com.google.common.collect.ImmutableList
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import java.util.function.Function

open class MachineModel(
    private val isPipe: Boolean,
    private val machineHullLocation: ResourceLocation
) : IModel {

    override fun getTextures(): Collection<ResourceLocation> {
        return ImmutableList.of(
            machineHullLocation,
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
        return if (isPipe) {
            PipedMachineBakedModel(machineHullLocation, bakedTextureGetter)
        } else {
            MachineBakedModel(machineHullLocation, bakedTextureGetter)
        }
    }
}