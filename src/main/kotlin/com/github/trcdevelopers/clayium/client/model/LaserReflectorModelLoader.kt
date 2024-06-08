package com.github.trcdevelopers.clayium.client.model

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import java.util.function.Function

object LaserReflectorModelLoader : ICustomModelLoader {
    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        return modelLocation.namespace == CValues.MOD_ID && "laser_reflector" in modelLocation.path
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        return object : IModel {

            override fun getTextures(): Collection<ResourceLocation> {
                return listOf(clayiumId("blocks/laserreflector"))
            }

            override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
                return object : IBakedModel {
                    override fun getQuads( state: IBlockState?, side: EnumFacing?, rand: Long ): List<BakedQuad> = emptyList()
                    override fun getParticleTexture(): TextureAtlasSprite  = bakedTextureGetter.apply(clayiumId("blocks/laserreflector"))

                    override fun isAmbientOcclusion() = true
                    override fun isGui3d() = true
                    override fun isBuiltInRenderer() = true
                    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE
                }
            }
        }
    }
}