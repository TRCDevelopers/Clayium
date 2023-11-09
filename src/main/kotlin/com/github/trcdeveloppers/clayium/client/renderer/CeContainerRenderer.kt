package com.github.trcdeveloppers.clayium.client.renderer

import com.github.trcdeveloppers.clayium.common.blocks.machine.TileClayiumContainer
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

class CeContainerRenderer : TileEntitySpecialRenderer<TileClayiumContainer>() {
    override fun render(
        te: TileClayiumContainer,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha)
    }
}