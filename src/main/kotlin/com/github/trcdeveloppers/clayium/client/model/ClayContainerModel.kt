package com.github.trcdeveloppers.clayium.client.model

import com.github.trcdeveloppers.clayium.common.blocks.machine.BlockSingleSlotMachine
import com.github.trcdeveloppers.clayium.common.blocks.machine.EnumImportMode
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.util.vector.Vector3f

@SideOnly(Side.CLIENT)
class ClayContainerModel(
    private val machineHullTier: Int = 4,
    private val faceTextureLocation: ResourceLocation,
    private val facing: EnumFacing,
) : IModel {

    override fun getTextures(): MutableCollection<ResourceLocation> {
        return mutableListOf(
            ResourceLocation("clayium:blocks/machinehull-$machineHullTier"),
            faceTextureLocation,
            ResourceLocation("clayium:blocks/import"),
            ResourceLocation("clayium:blocks/import_energy"),
            ResourceLocation("clayium:blocks/export"),
        )
    }

    override fun bake(
        state: IModelState,
        format: VertexFormat,
        bakedTextureGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>,
    ): IBakedModel {
        return ClayContainerBakedModel(bakedTextureGetter, machineHullTier, faceTextureLocation, facing)
    }


    private class ClayContainerBakedModel(
        bakedTextureGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>,
        tier: Int,
        faceTextureLocation: ResourceLocation,
        facing: EnumFacing,
    ) : IBakedModel {

        private val machineHull = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/machinehull-$tier"))
        private val faceTexture = bakedTextureGetter.apply(faceTextureLocation)
        private val import = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/import"))
        private val importEnergy = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/import_energy"))
        private val export = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/export"))

        private val baseQuads = EnumFacing.entries.associateWith { side ->
            if (side == facing) {
                arrayOf(getFaceQuad(side, this.machineHull), getFaceQuad(side, this.faceTexture))
            } else {
                arrayOf(this.getFaceQuad(side, this.machineHull))
            }
        }

        override fun isAmbientOcclusion() = true
        override fun isGui3d() = true
        override fun isBuiltInRenderer() = false

        override fun getParticleTexture(): TextureAtlasSprite = this.machineHull
        override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE

        override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
            if (side == null || state == null) {
                return emptyList()
            }

            val extState = state as IExtendedBlockState
            val quads = mutableListOf<BakedQuad>()

            // Base quad (machine hull, face texture)
            quads.addAll(this.baseQuads.getValue(side))

            // Import, Import CE
            when (extState.getValue(BlockSingleSlotMachine.getInputState(side))) {
                EnumImportMode.NORMAL -> quads.add(this.getFaceQuad(side, this.import))
                EnumImportMode.CE -> quads.add(this.getFaceQuad(side, this.importEnergy))
                EnumImportMode.NONE, null -> {}
            }

            // Export
            if (extState.getValue(BlockSingleSlotMachine.getOutputState(side))) {
                quads.add(this.getFaceQuad(side, this.export))
            }

            return quads
        }

        private fun getFaceQuad(side: EnumFacing, texture: TextureAtlasSprite): BakedQuad {
            return faceBakery.makeBakedQuad(
                Vector3f(0f, 0f, 0f),
                Vector3f(16f, 16f, 16f),
                BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(0f, 0f, 16f, 16f), 0)),
                texture,
                side, ModelRotation.X0_Y0,
                null, true, true,
            )
        }

        companion object {
            private val faceBakery = FaceBakery()
        }
    }
}