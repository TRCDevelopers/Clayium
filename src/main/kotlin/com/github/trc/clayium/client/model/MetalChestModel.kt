@file:Suppress("DEPRECATION")

package com.github.trc.clayium.client.model

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemTransformVec3f
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import org.lwjgl.util.vector.Vector3f
import java.util.function.Function

object MetalChestModel : IModel {

    private var bakedModel: MetalChestBakedModel? = null

    /**
     * same as the vanilla chest model (assets/minecraft/models/item/chest.json).
     */
    val chestItemCameraTransforms = run {
        /**
         * scale 0.0625 is found at deserialization: [net.minecraft.client.renderer.block.model.ItemTransformVec3f.Deserializer.deserialize]
         */
        val v = Vector3f(0f, 2.5f, 0f).apply { scale(0.0625F) }
        val zero = Vector3f(0f, 0f, 0f)
        val one = Vector3f(1f, 1f, 1f)
        // XXX person left is the same as right if not specified in the model json. [net.minecraft.client.renderer.block.model.ItemCameraTransforms.Deserializer.deserialize]
        // In the vanilla chest model, left is not specified, so it is the same as right.
        ItemCameraTransforms(
            /* third persion left */
            ItemTransformVec3f(Vector3f(75f, 315f, 0f), v, Vector3f(0.375f, 0.375f, 0.375f)),
            /* third persion right */
            ItemTransformVec3f(Vector3f(75f, 315f, 0f), v, Vector3f(0.375f, 0.375f, 0.375f)),
            /* first persion left */
            ItemTransformVec3f(Vector3f(0f, 315f, 0f), zero, Vector3f(0.4f, 0.4f, 0.4f)),
            /* first persion right */
            ItemTransformVec3f(Vector3f(0f, 315f, 0f), zero, Vector3f(0.4f, 0.4f, 0.4f)),
            /* head */
            ItemTransformVec3f(Vector3f(0f, 180f, 0f), zero, one),
            /* gui */
            ItemTransformVec3f(Vector3f(30f, 45f, 0f), zero, Vector3f(0.625f, 0.625f, 0.625f)),
            /* ground */
            ItemTransformVec3f(zero, Vector3f(0f, 3f, 0f), Vector3f(0.25f, 0.25f, 0.25f)),
            /* fixed */
            ItemTransformVec3f(Vector3f(0f, 180f, 0f), zero, one)
        )
    }

    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        return bakedModel ?: MetalChestBakedModel(bakedTextureGetter).also { bakedModel = it }
    }

    class MetalChestBakedModel(
        texGetter: Function<ResourceLocation, TextureAtlasSprite>
    ) : MaterialBlockBakedModel(texGetter) {
        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long) = emptyList<BakedQuad>()

        override fun isBuiltInRenderer() = true

        @Suppress("OVERRIDE_DEPRECATION")
        override fun getItemCameraTransforms(): ItemCameraTransforms {
            return chestItemCameraTransforms
        }
    }
}