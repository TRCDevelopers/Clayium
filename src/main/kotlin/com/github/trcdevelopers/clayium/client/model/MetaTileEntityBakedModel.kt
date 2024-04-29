package com.github.trcdevelopers.clayium.client.model

import com.github.trcdevelopers.clayium.api.block.BlockMachine.Companion.TILE_ENTITY
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState

class MetaTileEntityBakedModel : IBakedModel {

    private var machineHullTexture: TextureAtlasSprite? = null

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (state == null || side == null || state !is IExtendedBlockState) return emptyList()
        val mte = (state.getValue(TILE_ENTITY) as? MetaTileEntityHolder)?.metaTileEntity ?: return emptyList()

        if (machineHullTexture == null) machineHullTexture = ModelTextures.HULL_TEXTURES[mte.tier]

        return mutableListOf<BakedQuad>().apply {
            add(ModelTextures.HULL_QUADS[mte.tier][side] ?: return@apply)
        }

    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
    override fun isBuiltInRenderer() = false
    override fun getParticleTexture() = machineHullTexture ?: ModelTextures.MISSING
    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE
}