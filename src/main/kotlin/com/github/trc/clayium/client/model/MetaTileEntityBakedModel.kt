package com.github.trc.clayium.client.model

import codechicken.lib.render.particle.IModelParticleProvider
import codechicken.lib.texture.TextureUtils
import com.github.trc.clayium.api.block.BlockMachine.Companion.TILE_ENTITY
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trc.clayium.api.util.getMetaTileEntity
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.IExtendedBlockState

class MetaTileEntityBakedModel : IModelParticleProvider {

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (state == null || side == null || state !is IExtendedBlockState) return emptyList()
        val mte = (state.getValue(TILE_ENTITY) as? MetaTileEntityHolder)?.metaTileEntity ?: return emptyList()

        val quads = mutableListOf<BakedQuad>()
        mte.getQuads(quads, state, side, rand)
        mte.overlayQuads(quads, state, side, rand)
        ModelTextures.getInputQuad(mte.inputModes[side.index], side)?.let { quads.add(it) }
        ModelTextures.getOutputQuad(mte.outputModes[side.index], side)?.let { quads.add(it) }
        mte.filters[side.index]?.let { quads.add(ModelTextures.getFilterQuad(side)) }
        return quads
    }

    override fun getHitEffects(traceResult: RayTraceResult, state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite> {
        val metaTileEntity = world.getMetaTileEntity(pos) ?: return setOf(TextureUtils.getMissingSprite())
        return setOf(ModelTextures.getHullTexture(metaTileEntity.tier))
    }

    override fun getDestroyEffects(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite> {
        val metaTileEntity = world.getMetaTileEntity(pos) ?: return setOf(TextureUtils.getMissingSprite())
        return setOf(ModelTextures.getHullTexture(metaTileEntity.tier))
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
    override fun isBuiltInRenderer() = false
    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE
}