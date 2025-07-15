package com.github.trc.clayium.client.model

import codechicken.lib.render.particle.CustomParticleHandler
import codechicken.lib.render.particle.IModelParticleProvider
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.blocks.material.BlockCompressed
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.IExtendedBlockState

/**
 * handles particle for [com.github.trc.clayium.common.blocks.BlockMaterialBase].
 *
 * This class uses [IModelParticleProvider], so you have to call CCLib's [CustomParticleHandler].
 */
abstract class MaterialBlockBakedModel(
    private val texGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>
) : IModelParticleProvider {
    override fun getHitEffects(traceResult: RayTraceResult, state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite?> {
        return getParticle(state)
    }

    override fun getDestroyEffects(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Set<TextureAtlasSprite?> {
        return getParticle(state)
    }

    fun getParticle(state: IBlockState?): Set<TextureAtlasSprite> {
        val state = state as? IExtendedBlockState ?: return emptySet()
        val materialName = state.getValue(BlockCompressed.MATERIAL_NAME)
        val atlas = texGetter.apply(clayiumId("blocks/compressed_$materialName"))
        return setOf(atlas)
    }
}