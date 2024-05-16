package com.github.trcdevelopers.clayium.client.tesr

import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.common.blocks.TileEntityClayLaserReflector
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

object ClayLaserReflectorRenderer : TileEntitySpecialRenderer<TileEntityClayLaserReflector>() {
    override fun render(te: TileEntityClayLaserReflector, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        if (!te.isActive) return
        val laserSource = te.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER, null) ?: return

        ClayLaserRenderer.renderLaser(laserSource, x, y, z, this::bindTexture)
    }
}