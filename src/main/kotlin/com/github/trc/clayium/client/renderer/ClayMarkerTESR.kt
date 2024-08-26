package com.github.trc.clayium.client.renderer

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.common.blocks.marker.BlockClayMarker
import com.github.trc.clayium.common.blocks.marker.TileClayMarker
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

object ClayMarkerTESR : TileEntitySpecialRenderer<TileClayMarker>() {
    private val clayMarkerC6 = Cuboid6(BlockClayMarker.CLAY_MARKER_AABB)
    override fun render(te: TileClayMarker, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val range = te.rangeRelative ?: return
        AreaMarkerRenderer.render(clayMarkerC6, range, x, y, z, te.rangeRenderMode)
    }

    override fun isGlobalRenderer(te: TileClayMarker) = true
}