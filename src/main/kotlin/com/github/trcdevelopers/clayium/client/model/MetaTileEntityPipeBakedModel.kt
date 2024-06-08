package com.github.trcdevelopers.clayium.client.model

import codechicken.lib.render.particle.IModelParticleProvider
import codechicken.lib.texture.TextureUtils
import com.github.trcdevelopers.clayium.api.block.BlockMachine
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ITier
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.IExtendedBlockState
import org.lwjgl.util.vector.Vector3f

class MetaTileEntityPipeBakedModel : IModelParticleProvider {

    private val sideCubes = listOf(
        // From top to bottom, these are DOWN, UP, NORTH, SOUTH, WEST and EAST
        Pair(Vector3f(5f, 0f, 5f), Vector3f(11f, 5f, 11f)),
        Pair(Vector3f(5f, 11f, 5f), Vector3f(11f, 16f, 11f)),
        Pair(Vector3f(5f, 5f, 0f), Vector3f(11f, 11f, 5f)),
        Pair(Vector3f(5f, 5f, 11f), Vector3f(11f, 11f, 16f)),
        Pair(Vector3f(0f, 5f, 5f), Vector3f(5f, 11f, 11f)),
        Pair(Vector3f(11f, 5f, 5f), Vector3f(16f, 11f, 11f)),
    )

    // Tier -> Pipe Cube Pos -> Side of a Cube -> Quad
    val cubeQuads: Map<ITier, List<List<BakedQuad>>> = ClayTiers.entries.associateWith { iTier ->
        EnumFacing.entries.map { cubePos ->
            EnumFacing.entries
                .filter { it != cubePos.opposite }
                .map { quadSide ->
                    ModelTextures.faceBakery.makeBakedQuad(
                        sideCubes[cubePos.index].first,
                        sideCubes[cubePos.index].second,
                        BlockPartFace(null, 0, "", getUv(cubePos = cubePos, sideOfCube = quadSide)),
                        ModelTextures.getHullTexture(iTier),
                        quadSide, ModelRotation.X0_Y0,
                        null, true, true
                    )
                }
        }
    }

    // Tier -> Side of a Center Cube -> Quad
    val centerCubeQuads: Map<ITier, List<BakedQuad>> = ClayTiers.entries.associateWith { tier ->
        EnumFacing.entries.map {
            ModelTextures.faceBakery.makeBakedQuad(
                Vector3f(5f, 5f, 5f),
                Vector3f(11f, 11f, 11f),
                BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(5f, 5f, 11f, 11f), 0)),
                ModelTextures.getHullTexture(tier),
                it, ModelRotation.X0_Y0,
                null, true, true
            )
        }
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (state == null || side != null || state !is IExtendedBlockState) return emptyList()
        val metaTileEntity = (state.getValue(BlockMachine.TILE_ENTITY) as? MetaTileEntityHolder)?.metaTileEntity
            ?: return emptyList()
        val tier = metaTileEntity.tier
        val connections = metaTileEntity.connectionsCache

        val quads = mutableListOf<BakedQuad>()
        val sideCubeQuads = this.cubeQuads[tier] ?: return emptyList()
        val centerCubeQuads = this.centerCubeQuads[tier] ?: return emptyList()

        for (i in 0..5) {
            if (connections[i]) {
                quads.addAll(sideCubeQuads[i])
            } else {
                quads.addAll(centerCubeQuads)
            }
        }

        return quads
    }

    override fun getHitEffects(traceResult: RayTraceResult, state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite> {
        val metaTileEntity = CUtils.getMetaTileEntity(world, pos) ?: return setOf(TextureUtils.getMissingSprite())
        return setOf(ModelTextures.getHullTexture(metaTileEntity.tier))
    }

    override fun getDestroyEffects(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite> {
        val metaTileEntity = CUtils.getMetaTileEntity(world, pos) ?: return setOf(TextureUtils.getMissingSprite())
        return setOf(ModelTextures.getHullTexture(metaTileEntity.tier))
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
    override fun isBuiltInRenderer() = false
    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE

    private fun getUv(cubePos: EnumFacing, sideOfCube: EnumFacing): BlockFaceUV {
        if (cubePos == sideOfCube) {
            return BlockFaceUV(floatArrayOf(5f, 5f, 11f, 11f), 0)
        }
        return when (cubePos) {
            EnumFacing.DOWN -> BlockFaceUV(floatArrayOf(5f, 11f, 11f, 16f), 0)
            EnumFacing.UP -> BlockFaceUV(floatArrayOf(5f, 0f, 11f, 5f), 0)
            EnumFacing.NORTH -> when (sideOfCube) {
                EnumFacing.UP -> BlockFaceUV(floatArrayOf(5f, 0f, 11f, 5f), 0)
                EnumFacing.DOWN -> BlockFaceUV(floatArrayOf(5f, 11f, 11f, 16f), 0)
                EnumFacing.WEST -> BlockFaceUV(floatArrayOf(0f, 5f, 5f, 11f), 0)
                EnumFacing.EAST -> BlockFaceUV(floatArrayOf(11f, 5f, 16f, 11f), 0)
                else -> throw IllegalArgumentException("Invalid side of cube: $sideOfCube")
            }

            EnumFacing.SOUTH -> when (sideOfCube) {
                EnumFacing.UP -> BlockFaceUV(floatArrayOf(5f, 11f, 11f, 16f), 0)
                EnumFacing.DOWN -> BlockFaceUV(floatArrayOf(5f, 0f, 11f, 5f), 0)
                EnumFacing.WEST -> BlockFaceUV(floatArrayOf(11f, 5f, 16f, 11f), 0)
                EnumFacing.EAST -> BlockFaceUV(floatArrayOf(0f, 5f, 5f, 11f), 0)
                else -> throw IllegalArgumentException("Invalid side of cube: $sideOfCube")
            }

            EnumFacing.WEST -> when (sideOfCube) {
                EnumFacing.UP, EnumFacing.DOWN -> BlockFaceUV(floatArrayOf(0f, 5f, 5f, 11f), 0)
                EnumFacing.NORTH -> BlockFaceUV(floatArrayOf(11f, 5f, 16f, 11f), 0)
                EnumFacing.SOUTH -> BlockFaceUV(floatArrayOf(0f, 5f, 5f, 11f), 0)
                else -> throw IllegalArgumentException("Invalid side of cube: $sideOfCube")
            }

            EnumFacing.EAST -> when (sideOfCube) {
                EnumFacing.UP, EnumFacing.DOWN -> BlockFaceUV(floatArrayOf(11f, 5f, 16f, 11f), 0)
                EnumFacing.NORTH -> BlockFaceUV(floatArrayOf(0f, 5f, 5f, 11f), 0)
                EnumFacing.SOUTH -> BlockFaceUV(floatArrayOf(11f, 5f, 16f, 11f), 0)
                else -> throw IllegalArgumentException("Invalid side of cube: $sideOfCube")
            }
        }
    }
}