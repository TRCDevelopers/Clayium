package com.github.trc.clayium.client.renderer

import codechicken.lib.render.RenderUtils
import codechicken.lib.vec.Cuboid6
import net.minecraft.client.renderer.GlStateManager

object AreaMarkerRenderer {
    fun render(source: Cuboid6, area: Cuboid6, x: Double, y: Double, z: Double, mode: RangeRenderMode) {
        if (mode == RangeRenderMode.DISABLED) return

        GlStateManager.pushMatrix()
        CRenderUtils.enableTranslucent()
        if (mode == RangeRenderMode.ENABLED_XRAY) { CRenderUtils.enableXray() }
        run {
            GlStateManager.translate(x, y, z)
            GlStateManager.color(0.1f, 0.1f, 0.7f, 0.3f)
            RenderUtils.drawCuboidSolid(area)
            GlStateManager.color(0.1f, 0.1f, 0.7f, 1f)
            RenderUtils.drawCuboidOutline(area)
        }
        if (mode == RangeRenderMode.ENABLED_XRAY) { CRenderUtils.disableXray() }
        CRenderUtils.disableTranslucent()
        GlStateManager.popMatrix()
    }

    enum class RangeRenderMode {
        DISABLED, ENABLED, ENABLED_XRAY
    }
}