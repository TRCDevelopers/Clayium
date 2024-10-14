package com.github.trc.clayium.client.model

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.MachineIoMode.ALL
import com.github.trc.clayium.api.util.MachineIoMode.CE
import com.github.trc.clayium.api.util.MachineIoMode.FIRST
import com.github.trc.clayium.api.util.MachineIoMode.M_1
import com.github.trc.clayium.api.util.MachineIoMode.M_2
import com.github.trc.clayium.api.util.MachineIoMode.M_3
import com.github.trc.clayium.api.util.MachineIoMode.M_4
import com.github.trc.clayium.api.util.MachineIoMode.M_5
import com.github.trc.clayium.api.util.MachineIoMode.M_6
import com.github.trc.clayium.api.util.MachineIoMode.M_ALL
import com.github.trc.clayium.api.util.MachineIoMode.NONE
import com.github.trc.clayium.api.util.MachineIoMode.SECOND
import com.github.trc.clayium.api.util.clayiumId
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

    lateinit var MISSING: TextureAtlasSprite
        private set

    // metaTileEntity.faceTexture -> map
    private val _faceQuads: MutableMap<ResourceLocation, Map<EnumFacing, BakedQuad>> =
        mutableMapOf()
    val FACE_QUADS: Map<ResourceLocation, Map<EnumFacing, BakedQuad>>
        get() = _faceQuads

    private lateinit var HULL_QUADS: Map<String, Map<EnumFacing, BakedQuad>>
    private lateinit var HULL_TEXTURES: Map<String, TextureAtlasSprite>
    private lateinit var inputModeQuads: Map<MachineIoMode, List<BakedQuad>?>
    private lateinit var outputModeQuads: Map<MachineIoMode, List<BakedQuad>?>
    private lateinit var filterQuads: List<BakedQuad>

    val faceBakery = FaceBakery()

    fun createQuad(
        side: EnumFacing,
        texture: TextureAtlasSprite,
        uv: FloatArray = floatArrayOf(0f, 0f, 16f, 16f)
    ): BakedQuad {
        return faceBakery.makeBakedQuad(
            Vector3f(0f, 0f, 0f),
            Vector3f(16f, 16f, 16f),
            BlockPartFace(null, 0, "", BlockFaceUV(uv, 0)),
            texture,
            side,
            ModelRotation.X0_Y0,
            null,
            true,
            true,
        )
    }

    fun initialize(getter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>) {
        if (isInitialized) return
        isInitialized = true
        MISSING = getter.apply(ModelLoader.MODEL_MISSING)

        this.HULL_TEXTURES =
            ClayTiers.entries.associate { it.prefixTranslationKey to getter.apply(it.hullLocation) }

        this.HULL_QUADS =
            ClayTiers.entries.associate { tier ->
                tier.prefixTranslationKey to
                    (EnumFacing.VALUES.associateWith { side ->
                        createQuad(side, getHullTexture(tier))
                    })
            }

        _faceQuads.clear()
        for (registry in ClayiumApi.mteManager.allRegistries()) {
            for (metaTileEntity in registry) {
                metaTileEntity.bakeQuads(getter, faceBakery)
                metaTileEntity.requiredTextures.filterNotNull().forEach { faceTexture ->
                    _faceQuads.computeIfAbsent(faceTexture) {
                        EnumFacing.entries.associateWith { side ->
                            createQuad(side, getter.apply(faceTexture))
                        }
                    }
                }
            }
        }

        this.inputModeQuads =
            MachineIoMode.entries.associateWith { ioMode ->
                val textureName =
                    when (ioMode) {
                        NONE -> return@associateWith null
                        FIRST -> "import_1"
                        SECOND -> "import_2"
                        ALL -> "import"
                        CE -> "import_energy"
                        M_ALL -> "import_m0"
                        M_1 -> "import_m1"
                        M_2 -> "import_m2"
                        M_3 -> "import_m3"
                        M_4 -> "import_m4"
                        M_5 -> "import_m5"
                        M_6 -> "import_m6"
                    }
                val atlasSprite = getter.apply(clayiumId("blocks/$textureName"))
                EnumFacing.entries.map { side -> createQuad(side, atlasSprite) }
            }

        outputModeQuads =
            MachineIoMode.entries.associateWith { ioMode ->
                val textureName =
                    when (ioMode) {
                        NONE -> return@associateWith null
                        FIRST -> "export_1"
                        SECOND -> "export_2"
                        ALL -> "export"
                        CE -> return@associateWith null
                        M_ALL -> "export_m0"
                        M_1 -> "export_m1"
                        M_2 -> "export_m2"
                        M_3 -> "export_m3"
                        M_4 -> "export_m4"
                        M_5 -> "export_m5"
                        M_6 -> "export_m6"
                    }
                val atlasSprite = getter.apply(clayiumId("blocks/$textureName"))
                EnumFacing.entries.map { side -> createQuad(side, atlasSprite) }
            }

        val filterSprite = getter.apply(clayiumId("blocks/filter"))
        filterQuads = EnumFacing.entries.map { createQuad(it, filterSprite) }
    }

    fun getHullTexture(tier: ITier): TextureAtlasSprite {
        return HULL_TEXTURES[tier.prefixTranslationKey] ?: MISSING
    }

    fun getHullQuads(tier: ITier): Map<EnumFacing, BakedQuad>? {
        return HULL_QUADS[tier.prefixTranslationKey]
    }

    fun getInputQuad(ioMode: MachineIoMode, facing: EnumFacing): BakedQuad? {
        return inputModeQuads[ioMode]?.get(facing.index)
    }

    fun getOutputQuad(ioMode: MachineIoMode, facing: EnumFacing): BakedQuad? {
        return outputModeQuads[ioMode]?.get(facing.index)
    }

    fun getFilterQuad(side: EnumFacing): BakedQuad {
        return filterQuads[side.index]
    }
}
