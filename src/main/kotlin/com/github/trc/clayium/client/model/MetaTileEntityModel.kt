package com.github.trc.clayium.client.model

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.util.clayiumId
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
        return mutableSetOf<ResourceLocation>().apply {
            // machine face textures
            ClayiumApi.mteManager.allRegistries().forEach {
                it.forEach { metaTileEntity ->
                    addAll(metaTileEntity.requiredTextures.filterNotNull())
                }
            }
            // Block Breaker & Item Collector Back
            add(clayiumId("blocks/miner_back"))
            // io textures
            add(clayiumId("blocks/import_energy"))
            add(clayiumId("blocks/import"))
            add(clayiumId("blocks/import_1"))
            add(clayiumId("blocks/import_2"))
            add(clayiumId("blocks/import_12"))
            add(clayiumId("blocks/import_m0"))
            add(clayiumId("blocks/import_m1"))
            add(clayiumId("blocks/import_m2"))
            add(clayiumId("blocks/import_m3"))
            add(clayiumId("blocks/import_m4"))
            add(clayiumId("blocks/import_m5"))
            add(clayiumId("blocks/import_m6"))
            add(clayiumId("blocks/export"))
            add(clayiumId("blocks/export_1"))
            add(clayiumId("blocks/export_2"))
            add(clayiumId("blocks/export_12"))
            add(clayiumId("blocks/export_m0"))
            add(clayiumId("blocks/export_m1"))
            add(clayiumId("blocks/export_m2"))
            add(clayiumId("blocks/export_m3"))
            add(clayiumId("blocks/export_m4"))
            add(clayiumId("blocks/export_m5"))
            add(clayiumId("blocks/export_m6"))
            add(clayiumId("blocks/filter"))
            // machine hulls)
            add(clayiumId("blocks/machinehull_tier1"))
            add(clayiumId("blocks/machinehull_tier2"))
            add(clayiumId("blocks/machinehull_tier3"))
            add(clayiumId("blocks/machinehull_tier4"))
            add(clayiumId("blocks/machinehull_tier5"))
            add(clayiumId("blocks/machinehull_tier6"))
            add(clayiumId("blocks/machinehull_tier7"))
            add(clayiumId("blocks/machinehull_tier8"))
            add(clayiumId("blocks/machinehull_tier9"))
            add(clayiumId("blocks/machinehull_tier10"))
            add(clayiumId("blocks/machinehull_tier11"))
            add(clayiumId("blocks/machinehull_tier12"))
            add(clayiumId("blocks/machinehull_tier13"))
            add(clayiumId("blocks/az91d_hull"))
        }
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
            MetaTileEntityBakedModel()
        }
    }
}
