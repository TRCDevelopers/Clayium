package com.github.trcdevelopers.clayium.client

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import org.lwjgl.util.vector.Vector3f

object ModelUtils {

    val faceBakery = FaceBakery()

    val sideCubes = listOf(
        // From top to bottom, these are DOWN, UP, NORTH, SOUTH, WEST and EAST
        Pair(Vector3f(5f, 0f, 5f), Vector3f(11f, 5f, 11f)),
        Pair(Vector3f(5f, 11f, 5f), Vector3f(11f, 16f, 11f)),
        Pair(Vector3f(5f, 5f, 0f), Vector3f(11f, 11f, 5f)),
        Pair(Vector3f(5f, 5f, 11f), Vector3f(11f, 11f, 16f)),
        Pair(Vector3f(0f, 5f, 5f), Vector3f(5f, 11f, 11f)),
        Pair(Vector3f(11f, 5f, 5f), Vector3f(16f, 11f, 11f)),
    )

    private val uvCacheInt: Map<EnumFacing, Map<EnumFacing, IntArray>> = EnumFacing.entries.associateWith { cubePos ->
        EnumFacing.entries
            .filter { it != cubePos.opposite }
            .associateWith { facingOfCube ->
                getUv(cubePos, facingOfCube).map { it.toInt() }.toIntArray()
            }
    }

    private val uvCacheFloat: Map<EnumFacing, Map<EnumFacing, FloatArray>> = EnumFacing.entries.associateWith { cubePos ->
        EnumFacing.entries
            .filter { it != cubePos.opposite }
            .associateWith { facingOfCube ->
                getUv(cubePos, facingOfCube)
            }
    }

    fun createQuad(texture: TextureAtlasSprite, side: EnumFacing, from: Vector3f, to: Vector3f, uv: FloatArray): BakedQuad {
        return faceBakery.makeBakedQuad(
            from, to,
            BlockPartFace(null, 0, "", BlockFaceUV(uv, 0)),
            texture, side, ModelRotation.X0_Y0,
            null, true, true
        )
    }

    fun createSideCubeQuads(machineHull: TextureAtlasSprite): List<List<BakedQuad>> {
        return sideCubes.mapIndexed { index: Int, (from, to): Pair<Vector3f, Vector3f> ->
        val positionOfCube = EnumFacing.byIndex(index)
        EnumFacing.entries
            .filter { it != positionOfCube.opposite }
            .map { createQuad(machineHull, it, from, to, getUvFloat(positionOfCube, it)) }
        }
    }

    fun createCenterCubeQuads(machineHull: TextureAtlasSprite): List<BakedQuad> {
        return EnumFacing.entries.map{
            createQuad(machineHull, it, Vector3f(5f, 5f, 5f), Vector3f(11f, 11f, 11f), floatArrayOf(5f, 5f, 11f, 11f))
        }
    }

    /**
     * if `sideOfCube == cubePos.opposite`, this throws IllegalArgumentException because that face is always hided.
     */
    fun getUvInt(cubePos: EnumFacing, sideOfCube: EnumFacing): IntArray {
        return uvCacheInt[cubePos]?.get(sideOfCube) ?: throw IllegalArgumentException("Invisible side $sideOfCube of cube $cubePos")
    }

    /**
     * if `sideOfCube == cubePos.opposite`, this throws IllegalArgumentException because that face is always hided.
     */
    fun getUvFloat(cubePos: EnumFacing, sideOfCube: EnumFacing): FloatArray {
        return uvCacheFloat[cubePos]?.get(sideOfCube) ?: throw IllegalArgumentException("Invisible side $sideOfCube of cube $cubePos")
    }

    private fun getUv(cubePos: EnumFacing, sideOfCube: EnumFacing): FloatArray {
        if (cubePos == sideOfCube) {
            return floatArrayOf(5f, 5f, 11f, 11f)
        }
        return when (cubePos) {
            EnumFacing.DOWN -> floatArrayOf(5f, 11f, 11f, 16f)
            EnumFacing.UP -> floatArrayOf(5f, 0f, 11f, 5f)
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