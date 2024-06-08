package com.github.trcdevelopers.clayium.client.model

import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ITier
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

    private lateinit var HULL_QUADS: Map<String, Map<EnumFacing, BakedQuad>>

    private lateinit var HULL_TEXTURES: Map<String, TextureAtlasSprite>

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

        this.HULL_TEXTURES = ClayTiers.entries.associate {
            it.prefixTranslationKey to getter.apply(it.hullLocation)
        }
        this.HULL_QUADS = ClayTiers.entries.associate { tier ->
            tier.prefixTranslationKey to (EnumFacing.VALUES.associateWith { side ->
                createQuad(side, getHullTexture(tier))
            })
        }
        for (metaTileEntity in ClayiumApi.MTE_REGISTRY) {
            metaTileEntity.requiredTextures.filterNotNull().forEach { faceTexture ->
                _faceQuads.computeIfAbsent(faceTexture) {
                    EnumFacing.entries.associateWith { side ->
                        createQuad(side, getter.apply(faceTexture))
                    }
                }
            }
        }
        ClayiumApi.MTE_REGISTRY.forEach { it.bakeQuads(getter, faceBakery) }
        isInitialized = true
    }

    fun getHullTexture(tier: ITier): TextureAtlasSprite {
        return HULL_TEXTURES[tier.prefixTranslationKey] ?: MISSING
    }

    fun getHullQuads(tier: ITier): Map<EnumFacing, BakedQuad>? {
        return HULL_QUADS[tier.prefixTranslationKey]
    }
}