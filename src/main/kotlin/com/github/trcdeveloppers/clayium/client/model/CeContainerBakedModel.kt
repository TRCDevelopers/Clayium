package com.github.trcdeveloppers.clayium.client.model

import com.github.trcdeveloppers.clayium.common.blocks.machine.BlockSingleSlotMachine
import com.github.trcdeveloppers.clayium.common.blocks.machine.EnumImportMode
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
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
        quads.add(this.getFaceQuad(side, this.machineHull))

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