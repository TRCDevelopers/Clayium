package com.github.trcdeveloppers.clayium.client.model

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.common.blocks.machine.BlockSingleSlotMachine
import com.github.trcdeveloppers.clayium.common.blocks.machine.EnumImportMode
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.util.vector.Vector3f
import java.util.function.Function

@SideOnly(Side.CLIENT)
class CeContainerBakedModel(
    bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>,
    tier: Int,
) : IBakedModel {

    private val machineHull = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/machinehull-$tier"))
    private val import = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/import"))
    private val importEnergy = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/import_energy"))
    private val export = bakedTextureGetter.apply(ResourceLocation("clayium:blocks/export"))

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (side == null || state == null) {
            return emptyList()
        }

        val extState = state as IExtendedBlockState
        val quads = mutableListOf<BakedQuad>()

        // Machine Hull
        quads.add(
            faceBakery.makeBakedQuad(
                cubeFrom, cubeTo,
                BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(0f, 0f, 16f, 16f), 0)),
                this.machineHull,
                side, ModelRotation.X0_Y0,
                null, true, true
            )
        )

        when (extState.getValue(BlockSingleSlotMachine.getInputState(side))) {
            EnumImportMode.NORMAL -> quads.add(
                faceBakery.makeBakedQuad(
                    cubeFrom, cubeTo,
                    BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(0f, 0f, 16f, 16f), 0)),
                    this.import,
                    side, ModelRotation.X0_Y0,
                    null, true, true
                )
            )
            EnumImportMode.CE -> quads.add(
                faceBakery.makeBakedQuad(
                    cubeFrom, cubeTo,
                    BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(0f, 0f, 16f, 16f), 0)),
                    this.importEnergy,
                    side, ModelRotation.X0_Y0,
                    null, true, true
                )
            )
            EnumImportMode.NONE, null -> {}
        }

        if (extState.getValue(BlockSingleSlotMachine.getOutputState(side))) {
            quads.add(
                faceBakery.makeBakedQuad(
                    cubeFrom, cubeTo,
                    BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(0f, 0f, 16f, 16f), 0)),
                    this.export,
                    side, ModelRotation.X0_Y0,
                    null, true, true
                )
            )
        }

        return quads
    }

    override fun isAmbientOcclusion(): Boolean {
        return true
    }

    override fun isGui3d(): Boolean {
        return true
    }

    override fun isBuiltInRenderer(): Boolean {
        return false
    }

    override fun getParticleTexture(): TextureAtlasSprite {
        return this.machineHull
    }

    override fun getOverrides(): ItemOverrideList {
        return ItemOverrideList.NONE
    }

    companion object {
        private val textureGetter: Function<ResourceLocation, TextureAtlasSprite> = ModelLoader.defaultTextureGetter()

        private val cubeFrom = Vector3f(0f, 0f, 0f)
        private val cubeTo = Vector3f(16f, 16f, 16f)

        private val faceBakery = FaceBakery()
    }
}