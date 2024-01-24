package com.github.trcdevelopers.clayium.client

import net.minecraft.util.EnumFacing

object ModelUtils {

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