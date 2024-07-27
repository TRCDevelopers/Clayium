package com.github.trc.clayium.client.model

import codechicken.lib.render.particle.IModelParticleProvider
import codechicken.lib.texture.TextureUtils
import com.github.trc.clayium.api.block.BlockMachine.Companion.TILE_ENTITY
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.CUtils.clayiumId
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.MachineIoMode.*
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.IExtendedBlockState

class MetaTileEntityBakedModel(
    bakedTextureGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>,
) : IModelParticleProvider {

    private val inputModeQuads: Map<MachineIoMode, List<BakedQuad>?> = MachineIoMode.entries.associateWith { ioMode ->
        val textureName = when (ioMode) {
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
        val atlasSprite = bakedTextureGetter.apply(clayiumId("blocks/$textureName"))
        EnumFacing.entries.map { side ->
            ModelTextures.createQuad(side, atlasSprite)
        }
    }

    private val outputModeQuads: Map<MachineIoMode, List<BakedQuad>?> = MachineIoMode.entries.associateWith { ioMode ->
        val textureName = when (ioMode) {
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
        val atlasSprite = bakedTextureGetter.apply(clayiumId("blocks/$textureName"))
        EnumFacing.entries.map { side ->
            ModelTextures.createQuad(side, atlasSprite)
        }
    }

    private val filterQuads = EnumFacing.entries.map { side ->
        val atlasSprite = bakedTextureGetter.apply(clayiumId("blocks/filter"))
        ModelTextures.createQuad(side, atlasSprite)
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (state == null || side == null || state !is IExtendedBlockState) return emptyList()
        val mte = (state.getValue(TILE_ENTITY) as? MetaTileEntityHolder)?.metaTileEntity ?: return emptyList()

        val quads = mte.getQuads(state, side, rand)
        mte.overlayQuads(quads, state, side, rand)
        mte.inputModes.forEachIndexed { facingIndex, mteInputMode ->
            val side2Quad = inputModeQuads[mteInputMode] ?: return@forEachIndexed
            quads.add(side2Quad[facingIndex])
        }
        mte.outputModes.forEachIndexed { facingIndex, mteOutputMode ->
            val side2Quad = outputModeQuads[mteOutputMode] ?: return@forEachIndexed
            quads.add(side2Quad[facingIndex])
        }
        mte.filters.forEachIndexed { i, filter ->
            if (filter != null) quads.add(filterQuads[i])
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
}