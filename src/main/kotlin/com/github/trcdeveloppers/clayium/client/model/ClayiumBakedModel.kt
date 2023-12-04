package com.github.trcdeveloppers.clayium.client.model

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import org.lwjgl.util.vector.Vector3f


abstract class ClayiumBakedModel : IBakedModel {
    protected fun getFaceQuad(side: EnumFacing, texture: TextureAtlasSprite): BakedQuad {
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
        val faceBakery = FaceBakery()
    }
}