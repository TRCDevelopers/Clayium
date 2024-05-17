package com.github.trcdevelopers.clayium.client.renderer

import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.common.blocks.BlockClayLaserReflector
import com.github.trcdevelopers.clayium.common.blocks.TileEntityClayLaserReflector
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import org.lwjgl.opengl.GL11

object ClayLaserReflectorRenderer : TileEntitySpecialRenderer<TileEntityClayLaserReflector>() {
    override fun render(reflector: TileEntityClayLaserReflector, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableDepth()
        GlStateManager.depthMask(false)
        run {
            val reflectorFacing = world.getBlockState(reflector.pos).getValue(BlockClayLaserReflector.FACING)
            val tessellator = Tessellator.getInstance()
            val buf = tessellator.buffer
            this.bindTexture(clayiumId("textures/blocks/laserreflector.png"))

            GlStateManager.translate(x, y, z)
            GlStateManager.color(1f, 1f, 1f ,1f)

            GlStateManager.translate(0.5, 0.5, 0.5)
            when (reflectorFacing) {
                EnumFacing.DOWN -> GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f)
                EnumFacing.UP -> {}
                EnumFacing.NORTH -> GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
                EnumFacing.SOUTH -> GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f)
                EnumFacing.WEST -> GlStateManager.rotate(-90.0f, 0.0f, 0.0f, -1.0f)
                EnumFacing.EAST -> GlStateManager.rotate(90.0f, 0.0f, 0.0f, -1.0f)
            }
            GlStateManager.translate(-0.5, -0.5, -0.5)

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

        if (!reflector.isActive) return
        val laserSource = reflector.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER, null) ?: return

        ClayLaserRenderer.renderLaser(laserSource, x, y, z, this::bindTexture)
    }
}