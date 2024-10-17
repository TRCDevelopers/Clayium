package com.github.trc.clayium.client.renderer

import net.minecraft.client.renderer.GlStateManager

object CRenderUtils {
    fun enableTranslucent() {
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        GlStateManager.enableDepth()
        GlStateManager.depthMask(false)

        GlStateManager.disableTexture2D()
    }

    fun disableTranslucent() {
        GlStateManager.enableTexture2D()
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
    }

    fun enableXray() {
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.disableDepth()
    }

    fun disableXray() {
        GlStateManager.enableDepth()
        GlStateManager.enableCull()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture2D()
    }
}
