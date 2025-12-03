package io.github.trcdevelopers.clayium.client.renderer

import codechicken.lib.render.state.GlStateTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import org.lwjgl.opengl.GL11

object CRenderUtils {
    /**
     * Disables: lighting, depthMask.
     * Enables: blend, depth, blendFunc.
     *
     * You can use [codechicken.lib.render.state.GlStateTracker] to push/pop GL states around this call.
     * All the states modified by this method are tracked by GlStateTracker.
     */
    fun enableTranslucent() {
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        )
        GlStateManager.enableDepth()
        GlStateManager.depthMask(false)
    }

    /**
     * Disables: lighting, cullFace, depth.
     *
     * You can use [codechicken.lib.render.state.GlStateTracker] to push/pop GL states around this call.
     * All the states modified by this method are tracked by GlStateTracker.
     */
    fun enableXray() {
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.disableDepth()
    }

    /**
     * Draws given text with given colour and a semi-transparent black background at a current scale/position.
     */
    fun renderStringWithBackground(text: String, color: Int) {
        val mc = Minecraft.getMinecraft()
        GlStateManager.pushMatrix()
        GlStateTracker.pushState()
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f)
        GlStateManager.color(0f, 0f, 0f, 0.5f)
        GlStateManager.disableTexture2D()
        this.enableTranslucent()
        run {

            val width = mc.fontRenderer.getStringWidth(text) / 2
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
        GlStateTracker.popState()
        GlStateManager.popMatrix()
    }

    fun renderCube(aabb: AxisAlignedBB, r: Float, g: Float, b: Float, a: Float) {
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()

        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()

        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
        tessellator.draw()
    }
}