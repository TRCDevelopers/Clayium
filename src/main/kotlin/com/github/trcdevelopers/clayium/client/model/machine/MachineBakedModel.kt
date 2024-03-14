package com.github.trcdevelopers.clayium.client.model.machine

import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.ALL
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.CE
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.FIRST
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.SECOND
import com.google.common.collect.ImmutableList
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.property.IExtendedBlockState
import org.lwjgl.util.vector.Vector3f
import java.util.function.Function

class MachineBakedModel(
    private val facing: EnumFacing,
    faceLocation: ResourceLocation,
    machineHullLocation: ResourceLocation,
    bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>,
) : IBakedModel {

    private val machineHull = bakedTextureGetter.apply(machineHullLocation)
    private val face = bakedTextureGetter.apply(faceLocation)
    private val inputTextures: List<TextureAtlasSprite?> = MachineIoMode.entries.map {
        bakedTextureGetter.apply(getInputLocation(it) ?: return@map null)
    }
    private val outputTextures: List<TextureAtlasSprite?> = MachineIoMode.entries.map {
        bakedTextureGetter.apply(getOutputLocation(it) ?: return@map null)
    }

    private val machineHullQuads = EnumFacing.VALUES.map {
        createQuad(it, machineHull)
    }

    private val faceQuad = createQuad(facing, face)

    /**
     * value is null if the mode has no texture (i.e. NONE)
     */
    private val inputQuads: Map<MachineIoMode, List<BakedQuad>?> = MachineIoMode.entries.associateWith { mode ->
        EnumFacing.VALUES.map {
            val atlas = inputTextures[mode.id] ?: return@associateWith null
                createQuad(it, atlas)
        }
    }

    /**
     * value is null if the mode has no texture (i.e. NONE)
     */
    private val outputQuads: Map<MachineIoMode, List<BakedQuad>?> = MachineIoMode.entries.associateWith { mode ->
        EnumFacing.VALUES.map {
            val atlas = outputTextures[mode.id] ?: return@associateWith null
                createQuad(it, atlas)
        }
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (state == null || side == null) return emptyList()

        val extState = state as IExtendedBlockState
        val quads = mutableListOf(machineHullQuads[side.index])

        if (side == facing) quads.add(faceQuad)
        val inputMode = extState.getValue(BlockMachine.INPUTS)[side.index]
        inputQuads[inputMode]?.let { quads.add(it[side.index]) }

        val outputMode = extState.getValue(BlockMachine.OUTPUTS)[side.index]
        outputQuads[outputMode]?.let { quads.add(it[side.index]) }

        return ImmutableList.copyOf(quads)
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
    override fun isBuiltInRenderer() = false
    override fun getParticleTexture() = machineHull
    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE

    companion object {
        private val faceBakery = FaceBakery()

        private fun createQuad(side: EnumFacing, texture: TextureAtlasSprite): BakedQuad {
            return faceBakery.makeBakedQuad(
                Vector3f(0f, 0f, 0f),
                Vector3f(16f, 16f, 16f),
                BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(0f, 0f, 16f, 16f), 0)),
                texture,
                side, ModelRotation.X0_Y0,
                null, true, true,
            )
        }

        private fun getInputLocation(mode: MachineIoMode): ResourceLocation? {
            return when (mode) {
                FIRST -> ResourceLocation("clayium:blocks/import_1")
                SECOND -> ResourceLocation("clayium:blocks/import_2")
                ALL -> ResourceLocation("clayium:blocks/import")
                CE -> ResourceLocation("clayium:blocks/import_energy")
                else -> null
            }
        }

        private fun getOutputLocation(mode: MachineIoMode): ResourceLocation? {
            return when (mode) {
                FIRST -> ResourceLocation("clayium:blocks/export_1")
                SECOND -> ResourceLocation("clayium:blocks/export_2")
                ALL -> ResourceLocation("clayium:blocks/export")
                else -> null
            }
        }
    }
}