package io.github.trcdevelopers.clayium.client.renderer

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
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

    /**
     * Draws given text with given color and a semi-transparent black background at a current scale/position.
     */
    fun renderStringWithBackground(text: String, color: Int) {
        val mc = Minecraft.getMinecraft()
        GlStateManager.pushMatrix()
        val memory = memoryCurrentStates()
        run {
            GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f)
            val width = mc.fontRenderer.getStringWidth(text) / 2

            GlStateManager.color(0f, 0f, 0f, 0.5f)
            this.enableTranslucent()
            GlStateManager.disableTexture2D()
            val tessellator = Tessellator.getInstance()
            val bufferBuilder = tessellator.buffer
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION)
            bufferBuilder.pos(-width - 1.0, -1.0, 0.0).endVertex()
            bufferBuilder.pos(-width - 1.0, 8.0, 0.0).endVertex()
            bufferBuilder.pos(width + 1.0, 8.0, 0.0).endVertex()
            bufferBuilder.pos(width + 1.0, -1.0, 0.0).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
            GlStateManager.depthMask(true)

            Minecraft.getMinecraft().fontRenderer.drawString(text, -width, 0, color)
        }
        this.restoreStates(memory)
        GlStateManager.popMatrix()
    }

    fun memoryCurrentStates(): GlStatesInformation {
        return GlStatesInformation()
    }

    fun restoreStates(states: GlStatesInformation) {
        GlStateManager.color(states.r, states.g, states.b, states.a)
        GlStateManager.shadeModel(states.shadeModel)
        GlStateManager.bindTexture(states.textureId)
        if (states.lighting) GlStateManager.enableLighting() else GlStateManager.disableLighting()
        if (states.texture2D) GlStateManager.enableTexture2D() else GlStateManager.disableTexture2D()
        if (states.alphaTest) GlStateManager.enableAlpha() else GlStateManager.disableAlpha()
        if (states.depthTest) GlStateManager.enableDepth() else GlStateManager.disableDepth()
        if (states.depthMask) GlStateManager.depthMask(true) else GlStateManager.depthMask(false)
        if (states.cullFace) GlStateManager.enableCull() else GlStateManager.disableCull()
        // Blend
        if (states.blend) {
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(
                states.blendSrc, states.blendDst,
                states.blendSrcAlpha, states.blendDstAlpha
            )
        } else {
            GlStateManager.disableBlend()
        }
    }

    class GlStatesInformation(
        val lighting: Boolean = GlStateManager.lightingState.currentState,
        val texture2D: Boolean = GL11.glIsEnabled(GL11.GL_TEXTURE_2D),
        val alphaTest: Boolean = GlStateManager.alphaState.alphaTest.currentState,
        val depthTest: Boolean = GlStateManager.depthState.depthTest.currentState,
        val depthMask: Boolean = GlStateManager.depthState.maskEnabled,
        val cullFace: Boolean = GlStateManager.cullState.cullFace.currentState,
    ) {

        val r: Float
        val g: Float
        val b: Float
        val a: Float
        val textureId: Int
        val shadeModel: Int

        val blend = GlStateManager.blendState.blend.currentState
        val blendSrc = GlStateManager.blendState.srcFactor
        val blendSrcAlpha = GlStateManager.blendState.srcFactorAlpha
        val blendDst = GlStateManager.blendState.dstFactor
        val blendDstAlpha = GlStateManager.blendState.dstFactorAlpha

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