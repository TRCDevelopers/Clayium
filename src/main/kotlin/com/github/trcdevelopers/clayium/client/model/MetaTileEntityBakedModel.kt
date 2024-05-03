package com.github.trcdevelopers.clayium.client.model

import codechicken.lib.render.particle.IModelParticleProvider
import codechicken.lib.texture.TextureUtils
import com.github.trcdevelopers.clayium.api.block.BlockMachine.Companion.TILE_ENTITY
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trcdevelopers.clayium.api.util.CUtils
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

        return mutableListOf<BakedQuad>().apply {
            add(ModelTextures.HULL_QUADS[mte.tier][side] ?: return@apply)
        }

    }

    override fun getHitEffects(traceResult: RayTraceResult, state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite> {
        val metaTileEntity = CUtils.getMetaTileEntity(world, pos) ?: return setOf(TextureUtils.getMissingSprite())
        return setOf(ModelTextures.HULL_TEXTURES[metaTileEntity.tier])
    }

    override fun getDestroyEffects(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite> {
        val metaTileEntity = CUtils.getMetaTileEntity(world, pos) ?: return setOf(TextureUtils.getMissingSprite())
        return setOf(ModelTextures.HULL_TEXTURES[metaTileEntity.tier])
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
    override fun isBuiltInRenderer() = false
    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE
}