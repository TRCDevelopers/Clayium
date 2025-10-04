package io.github.trcdevelopers.clayium.client.renderer

import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11

object CRenderUtils {
    fun enableTranslucent() {
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
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

    fun memoryCurrentStates(): GlStatesInformation {
        return GlStatesInformation()
    }

    fun restoreStates(states: GlStatesInformation) {
        if (states.blend) GlStateManager.enableBlend() else GlStateManager.disableBlend()
        if (states.lighting) GlStateManager.enableLighting() else GlStateManager.disableLighting()
        if (states.texture2D) GlStateManager.enableTexture2D() else GlStateManager.disableTexture2D()
        if (states.alphaTest) GlStateManager.enableAlpha() else GlStateManager.disableAlpha()
        if (states.depthTest) GlStateManager.enableDepth() else GlStateManager.disableDepth()
        if (states.cullFace) GlStateManager.enableCull() else GlStateManager.disableCull()

        GlStateManager.color(states.r, states.g, states.b, states.a)
        GlStateManager.shadeModel(states.shadeModel)
        GlStateManager.bindTexture(states.textureId)
    }

    class GlStatesInformation(
        val blend: Boolean = GL11.glIsEnabled(GL11.GL_BLEND),
        val lighting: Boolean = GL11.glIsEnabled(GL11.GL_LIGHTING),
        val texture2D: Boolean = GL11.glIsEnabled(GL11.GL_TEXTURE_2D),
        val alphaTest: Boolean = GL11.glIsEnabled(GL11.GL_ALPHA_TEST),
        val depthTest: Boolean = GL11.glIsEnabled(GL11.GL_DEPTH_TEST),
        val cullFace: Boolean = GL11.glIsEnabled(GL11.GL_CULL_FACE),
    ) {

        val r: Float
        val g: Float
        val b: Float
        val a: Float
        val textureId: Int
        val shadeModel: Int

        init {
            // Minimal size 16.
            // java.lang.IllegalArgumentException: Number of remaining buffer elements is 4, must be at least 16.
            // Because at most 16 elements can be returned, a buffer with at least 16 elements is required, regardless of actual returned element count
            val rgba = BufferUtils.createFloatBuffer(16)
            GL11.glGetFloat(GL11.GL_CURRENT_COLOR, rgba)
            r = rgba[0]
            g = rgba[1]
            b = rgba[2]
            a = rgba[3]

            // Use non-buffer version, because buffer version is shifted by 8 bits.
            this.textureId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
            this.shadeModel = GL11.glGetInteger(GL11.GL_SHADE_MODEL)
        }
    }
}