package com.github.trcdevelopers.clayium.client.model.facedmachine

import com.github.trcdevelopers.clayium.client.model.machine.MachineBakedModel
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.function.Function

class FacedMachineBakedModel(
    private val facing: EnumFacing,
    faceLocation: ResourceLocation,
    machineHullLocation: ResourceLocation,
    bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>,
) : MachineBakedModel(machineHullLocation, bakedTextureGetter) {

    private val faceQuad = createQuad(facing, bakedTextureGetter.apply(faceLocation))

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        if (state == null || side == null) return emptyList()

        val quads = getBaseQuads(side)
        if (side == facing) quads.add(faceQuad)
        addIoQuads(quads, state as IExtendedBlockState, side, rand)
        return quads
    }

}