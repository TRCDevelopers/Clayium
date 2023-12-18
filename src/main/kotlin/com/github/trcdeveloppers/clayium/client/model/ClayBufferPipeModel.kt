package com.github.trcdeveloppers.clayium.client.model

import com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer.BlockClayBuffer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.property.IExtendedBlockState
import org.lwjgl.util.vector.Vector3f
import java.util.function.Function

class ClayBufferPipeModel(
    private val tier: Int,
) : IModel {

    override fun getTextures(): Collection<ResourceLocation> {
        return listOf(ResourceLocation("clayium:blocks/machinehull-${tier-1}"),)
    }

    override fun bake(
        state: IModelState,
        format: VertexFormat,
        bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>
    ): IBakedModel {
        return ClayBufferPipeBakedModel(bakedTextureGetter, tier)
    }

    private class ClayBufferPipeBakedModel(
        bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>,
        tier: Int,
    ) : IBakedModel {

        private val machineHull = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/machinehull-${tier-1}"))

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (state == null || side != null) return emptyList()

            val connections = (state as IExtendedBlockState).getValue(BlockClayBuffer.CONNECTIONS)
            val quads = mutableListOf<BakedQuad>()

            // Render the center cube (6, 6, 6) -> (11, 11, 11) if there is no connection
            val centerCubeRenderFlag = BooleanArray(6) { !connections[it] }

            // make shape
            val cubes = mutableSetOf(Vector3f(5f, 5f, 5f) to Vector3f(11f, 11f, 11f))
            for (enumfacing in EnumFacing.entries) {
                if (connections[enumfacing.index]) cubes.add(sideCubes[enumfacing] ?: continue)
            }

            // make quads for shape
            for (cube in cubes) {
                val (from, to) = cube
                val uv = floatArrayOf(from.x, from.y, to.x, to.y)
                for (enumfacing in EnumFacing.entries) {
                    if (connections[enumfacing.index]) {
                        quads.add(createQuad(enumfacing, from, to, uv))
                    }
                }
            }

            // add quads for center cube if needed
            for (enumfacing in EnumFacing.entries) {
                if (centerCubeRenderFlag[enumfacing.index]) {
                    quads.add(createQuad(enumfacing, Vector3f(5f, 5f, 5f), Vector3f(11f, 11f, 11f), floatArrayOf(5f, 5f, 11f, 11f)))
                }
            }

            return quads
        }

        override fun isAmbientOcclusion() = true
        override fun isGui3d() = true
        override fun isBuiltInRenderer() = false
        override fun getParticleTexture() = machineHull
        override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE

        private fun createQuad(side: EnumFacing, from: Vector3f, to: Vector3f, uv: FloatArray): BakedQuad {
            return faceBakery.makeBakedQuad(
                from, to,
                BlockPartFace(null, 0, "", BlockFaceUV(uv, 0)),
                machineHull, side, ModelRotation.X0_Y0,
                null, true, true

            )
        }

        companion object {
            private val faceBakery = FaceBakery()
            private val sideCubes: Map<EnumFacing, Pair<Vector3f, Vector3f>> = mapOf(
                EnumFacing.UP to (Vector3f(5f, 0f, 5f) to Vector3f(11f, 6f, 11f)),
                EnumFacing.DOWN to (Vector3f(5f, 11f, 5f) to Vector3f(11f, 16f, 11f)),
                EnumFacing.NORTH to (Vector3f(5f, 5f, 0f) to Vector3f(11f, 11f, 5f)),
                EnumFacing.SOUTH to (Vector3f(5f, 5f, 11f) to Vector3f(11f, 11f, 16f)),
                EnumFacing.WEST to (Vector3f(0f, 6f, 6f) to Vector3f(5f, 11f, 11f)),
                EnumFacing.EAST to (Vector3f(11f, 6f, 6f) to Vector3f(16f, 11f, 11f)),
            )
        }
    }
}