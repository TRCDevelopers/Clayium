package com.github.trc.clayium.client.renderer

import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11

object LaserReflectorItemStackRenderer : TileEntityItemStackRenderer() {
    override fun renderByItem(itemStackIn: ItemStack) {
        GlStateManager.pushMatrix()
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableDepth()
        GlStateManager.depthMask(false)
        run {
            val tessellator = Tessellator.getInstance()
            val buf = tessellator.buffer

            GlStateManager.color(1f, 1f, 1f, 1f)

            GlStateManager.translate(0.5, 0.5, 0.5)
            GlStateManager.scale(0.8f, 0.8f, 0.8f)
            GlStateManager.rotate(110f, 1f, 0f, 0f)
            GlStateManager.rotate(45f, 0f, 0f, 1f)
            GlStateManager.translate(-0.5, -0.5, -0.5)
            GlStateManager.translate(0.0, 0.1, 0.0)

            TileEntityRendererDispatcher.instance.renderEngine.bindTexture(
                clayiumId("textures/blocks/laserreflector.png")
            )

            val offset = 0.125
            val offset2 = offset * 2
            val xz0d = 0 + offset2
            val xz1d = 1 - offset2
            val yd = 0 + offset

            // draw base
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            buf.pos(xz0d, yd, xz0d).tex(0.0, 0.0).endVertex()
            buf.pos(xz1d, yd, xz0d).tex(1.0, 0.0).endVertex()
            buf.pos(xz1d, yd, xz1d).tex(1.0, 1.0).endVertex()
            buf.pos(xz0d, yd, xz1d).tex(0.0, 1.0).endVertex()
            tessellator.draw()

            val d0 = 1 - offset
            @Suppress("unused")
            for (unused in 0..3) {
                buf.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX)
                buf.pos(xz0d, yd, xz0d).tex(0.0, 0.0).endVertex()
                buf.pos(0.5, d0, 0.5).tex(0.5, 0.5).endVertex()
                buf.pos(xz1d, yd, xz0d).tex(1.0, 0.0).endVertex()
                tessellator.draw()
                GlStateManager.translate(0.5, 0.5, 0.5)
                GlStateManager.rotate(90f, 0f, 1f, 0f)
                GlStateManager.translate(-0.5, -0.5, -0.5)
            }
        }
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.popMatrix()
    }
}
