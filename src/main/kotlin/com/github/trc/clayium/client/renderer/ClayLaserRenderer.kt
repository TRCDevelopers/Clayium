package com.github.trc.clayium.client.renderer

import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.config.ConfigCore
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

object ClayLaserRenderer {
    fun renderLaser(
        laser: ClayLaser, direction: EnumFacing, length: Int, x: Double, y: Double, z: Double,
        bindTexture: (ResourceLocation) -> Unit,
    ) {
        val rawLaserRed = laser.red
        val rawLaserGreen = laser.green
        val rawLaserBlue = laser.blue
        val max = maxOf(rawLaserRed, rawLaserGreen, rawLaserBlue)
        if (max == 0) return

        //reference: https://qiita.com/KrGit3/items/6b2673c6362a3f88ef7a
        GlStateManager.pushMatrix()
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableDepth()
        GlStateManager.disableCull()
        GlStateManager.depthMask(false)
        run {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.75f)
            GlStateManager.translate(x + 0.5f, y + 0.5f, z + 0.5f)

            when (direction) {
                DOWN -> GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f)
                UP -> GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f)
                NORTH -> GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
                SOUTH -> GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f)
                WEST -> GlStateManager.rotate(-90.0f, 0.0f, 0.0f, -1.0f)
                EAST -> GlStateManager.rotate(90.0f, 0.0f, 0.0f, -1.0f)
            }

            val str = rawLaserRed + rawLaserGreen + rawLaserBlue
            val scale = 1.0f - 14.0625f / (str + 14)

            GlStateManager.scale(scale, 1.0f, scale)
            GlStateManager.translate(0.0f, -scale / 6.0f, 0.0f)
            GlStateManager.scale(1.0f, length + scale / 3.0f, 1.0f)

            bindTexture(clayiumId("textures/blocks/laser.png"))

            val tessellator = Tessellator.getInstance()
            val bufferBuilder = tessellator.buffer

            val laserQuality = ConfigCore.rendering.laserQuality

            val r = 255 * rawLaserRed / max
            val g = 255 * rawLaserGreen / max
            val b = 255 * rawLaserBlue / max
            val a = ((26.0f + scale * 26.0f) * 8 / laserQuality).toInt()

            @Suppress("unused")
            for (notUsed in 0..laserQuality) {
                bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
                bufferBuilder.pos(0.0, 1.0, 0.5).tex(0.0, 0.0).color(r, g, b, a).endVertex()
                bufferBuilder.pos(0.0, 1.0, -0.5).tex(1.0, 0.0).color(r, g, b, a).endVertex()
                bufferBuilder.pos(0.0, 0.0, -0.5).tex(1.0, 1.0).color(r, g, b, a).endVertex()
                bufferBuilder.pos(0.0, 0.0, 0.5).tex(0.0, 1.0).color(r, g, b, a).endVertex()
                tessellator.draw()
                GlStateManager.rotate(180.0f / laserQuality, 0.0f, 1.0f, 0.0f)
            }
        }
        GlStateManager.enableCull()
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.popMatrix()
    }
}