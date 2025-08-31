package io.github.trcdevelopers.clayium.client.model

import codechicken.lib.render.particle.CustomParticleHandler
import codechicken.lib.render.particle.IModelParticleProvider
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.blocks.material.BlockMaterialWithDynModel
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.IExtendedBlockState

/**
 * handles particle for [io.github.trcdevelopers.clayium.common.blocks.material.BlockMaterialBase].
 *
 * This class uses [IModelParticleProvider], so you have to call CCLib's [CustomParticleHandler].
 *
 * @see io.github.trcdevelopers.clayium.client.gui.TextureExtra
 * @see io.github.trcdevelopers.clayium.client.ClientProxy.onTextureStitchPre
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
        val materialName = state.getValue(BlockMaterialWithDynModel.MATERIAL_NAME)
        val atlas = texGetter.apply(clayiumId("blocks/compressed_$materialName"))
        return setOf(atlas)
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
    override fun isBuiltInRenderer() = false

    override fun getOverrides(): ItemOverrideList {
        return ItemOverrideList.NONE
    }
}