package com.github.trc.clayium.client.renderer

import codechicken.lib.render.RenderUtils
import codechicken.lib.vec.Cuboid6
import net.minecraft.client.renderer.GlStateManager

object AreaMarkerRenderer {
    fun render(source: Cuboid6, area: Cuboid6, x: Double, y: Double, z: Double, mode: RangeRenderMode) {
        if (mode == RangeRenderMode.DISABLED) return
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)
        renderSourceOverlay(source)
        renderRangeOverlay(area, mode == RangeRenderMode.ENABLED_XRAY)
        GlStateManager.popMatrix()
    }

    private fun renderSourceOverlay(source: Cuboid6) {
        GlStateManager.pushMatrix()
        CRenderUtils.enableTranslucent()
        CRenderUtils.enableXray()
        run {
            GlStateManager.color(0.7f, 0.1f, 0.1f, 0.3f)
            RenderUtils.drawCuboidSolid(source)
            GlStateManager.color(0.7f, 0.1f, 0.1f, 1f)
            RenderUtils.drawCuboidOutline(source)
        }
        CRenderUtils.disableXray()
        CRenderUtils.disableTranslucent()
        GlStateManager.popMatrix()
    }

    private fun renderRangeOverlay(range: Cuboid6, xray: Boolean) {
        GlStateManager.pushMatrix()
        CRenderUtils.enableTranslucent()
        if (xray) { CRenderUtils.enableXray() }
        run {
            GlStateManager.color(0.1f, 0.1f, 0.7f, 0.3f)
            RenderUtils.drawCuboidSolid(range)
            GlStateManager.color(0.1f, 0.1f, 0.7f, 1f)
            RenderUtils.drawCuboidOutline(range)
        }
        if (xray) { CRenderUtils.disableXray() }
        CRenderUtils.disableTranslucent()
        GlStateManager.popMatrix()
    }

    enum class RangeRenderMode {
        DISABLED, ENABLED, ENABLED_XRAY
    }
}