package com.github.trc.clayium.client.renderer

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.common.blocks.marker.TileClayMarker
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

object ClayMarkerTESR : TileEntitySpecialRenderer<TileClayMarker>() {
    override fun render(te: TileClayMarker, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val range = te.rangeRelative ?: return
        AreaMarkerRenderer.render(Cuboid6(), range, x, y, z, true)
    }
}