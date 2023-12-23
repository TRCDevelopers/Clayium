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
        return listOf(ResourceLocation("clayium:blocks/machinehull_tier$tier"),)
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
        machineHullTier: Int,
    ) : IBakedModel {

        private val machineHull = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/machinehull_tier$machineHullTier"))
        private val sideCubeQuads = sideCubes.mapIndexed { index: Int, (from, to): Pair<Vector3f, Vector3f> ->
            val positionOfCube = EnumFacing.byIndex(index)
            EnumFacing.entries
                .filter { it != positionOfCube.opposite }
                .map { createQuad(it, from, to, getUv(positionOfCube, it)) }
        }

        private val centerCubeQuad = EnumFacing.entries.map{
            createQuad(it, Vector3f(5f, 5f, 5f), Vector3f(11f, 11f, 11f), floatArrayOf(5f, 5f, 11f, 11f))
        }

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (state == null || side != null) return emptyList()

            val connections = (state as IExtendedBlockState).getValue(BlockClayBuffer.CONNECTIONS)
            val quads = mutableListOf<BakedQuad>()

            for (index in EnumFacing.entries.indices) {
                if (connections[index]) {
                    quads.addAll(sideCubeQuads[index])
                } else {
                    quads.add(centerCubeQuad[index])
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
            private val sideCubes = listOf(
                // From top to bottom, these are DOWN, UP, NORTH, SOUTH, WEST and EAST
                Pair(Vector3f(5f, 0f, 5f), Vector3f(11f, 5f, 11f)),
                Pair(Vector3f(5f, 11f, 5f), Vector3f(11f, 16f, 11f)),
                Pair(Vector3f(5f, 5f, 0f), Vector3f(11f, 11f, 5f)),
                Pair(Vector3f(5f, 5f, 11f), Vector3f(11f, 11f, 16f)),
                Pair(Vector3f(0f, 5f, 5f), Vector3f(5f, 11f, 11f)),
                Pair(Vector3f(11f, 5f, 5f), Vector3f(16f, 11f, 11f)),
            )

            private fun getUv(cubePos: EnumFacing, sideOfCube: EnumFacing): FloatArray {
                if (cubePos == sideOfCube) {
                    return floatArrayOf(5f, 5f, 11f, 11f)
                }
                return when (cubePos) {
                    EnumFacing.UP -> floatArrayOf(5f, 11f, 11f, 16f)
                    EnumFacing.DOWN -> floatArrayOf(5f, 0f, 11f, 5f)
                    EnumFacing.NORTH -> when (sideOfCube) {
                        EnumFacing.UP -> floatArrayOf(5f, 0f, 11f, 5f)
                        EnumFacing.DOWN -> floatArrayOf(5f, 11f, 11f, 16f)
                        EnumFacing.WEST -> floatArrayOf(0f, 5f, 5f, 11f)
                        EnumFacing.EAST -> floatArrayOf(11f, 5f, 16f, 11f)
                        else -> throw IllegalArgumentException("Invalid side of cube: $sideOfCube")
                    }
                    EnumFacing.SOUTH -> when (sideOfCube) {
                        EnumFacing.UP -> floatArrayOf(5f, 11f, 11f, 16f)
                        EnumFacing.DOWN -> floatArrayOf(5f, 0f, 11f, 5f)
                        EnumFacing.WEST -> floatArrayOf(11f, 5f, 16f, 11f)
                        EnumFacing.EAST -> floatArrayOf(0f, 5f, 5f, 11f)
                        else -> throw IllegalArgumentException("Invalid side of cube: $sideOfCube")
                    }
                    EnumFacing.WEST -> when (sideOfCube) {
                        EnumFacing.UP, EnumFacing.DOWN -> floatArrayOf(0f, 5f, 5f, 11f)
                        EnumFacing.NORTH -> floatArrayOf(11f, 5f, 16f, 11f)
                        EnumFacing.SOUTH -> floatArrayOf(0f, 5f, 5f, 11f)
                        else -> throw IllegalArgumentException("Invalid side of cube: $sideOfCube")
                    }
                    EnumFacing.EAST -> when (sideOfCube) {
                        EnumFacing.UP, EnumFacing.DOWN -> floatArrayOf(11f, 5f, 16f, 11f)
                        EnumFacing.NORTH -> floatArrayOf(0f, 5f, 5f, 11f)
                        EnumFacing.SOUTH -> floatArrayOf(11f, 5f, 16f, 11f)
                        else -> throw IllegalArgumentException("Invalid side of cube: $sideOfCube")
                    }
                }
            }
        }
    }
}