package com.github.trcdevelopers.clayium.client.model

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.ClayiumApi
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.util.vector.Vector3f

@SideOnly(Side.CLIENT)
object ModelTextures {

    var isInitialized = false
        private set

    lateinit var MISSING: TextureAtlasSprite
        private set

    lateinit var HULL_QUADS: List<Map<EnumFacing, BakedQuad>>
        private set

    lateinit var HULL_TEXTURES: List<TextureAtlasSprite>
        private set

    // metaTileEntity.faceTexture -> map
    private val _faceQuads: MutableMap<ResourceLocation, Map<EnumFacing, BakedQuad>> = mutableMapOf()
    val FACE_QUADS: Map<ResourceLocation, Map<EnumFacing, BakedQuad>> get() = _faceQuads

    val faceBakery = FaceBakery()
    fun createQuad(side: EnumFacing, texture: TextureAtlasSprite): BakedQuad {
        return faceBakery.makeBakedQuad(
            Vector3f(0f, 0f, 0f),
            Vector3f(16f, 16f, 16f),
            BlockPartFace(null, 0, "", BlockFaceUV(floatArrayOf(0f, 0f, 16f, 16f), 0)),
            texture,
            side, ModelRotation.X0_Y0,
            null, true, true,
        )
    }

    fun initialize(getter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>) {
        if (isInitialized) return
        MISSING = getter.apply(ModelLoader.MODEL_MISSING)

        this.HULL_TEXTURES = (0..13).map { i ->
            if (i == 0) MISSING
            getter.apply(ResourceLocation(CValues.MOD_ID, "blocks/machinehull_tier$i"))
        }
        this.HULL_QUADS = mutableListOf<Map<EnumFacing, BakedQuad>>().apply {
            (0..13).forEach { i ->
                add(EnumFacing.VALUES.associateWith { createQuad(it, HULL_TEXTURES[i]) })
            }
        }
        for (metaTileEntity in ClayiumApi.MTE_REGISTRY) {
            val faceTexture = metaTileEntity.faceTexture ?: continue
            _faceQuads.computeIfAbsent(faceTexture) {
                EnumFacing.entries.associateWith { side ->
                    createQuad(side, getter.apply(faceTexture))
                }
            }
        }
        isInitialized = true
    }

}