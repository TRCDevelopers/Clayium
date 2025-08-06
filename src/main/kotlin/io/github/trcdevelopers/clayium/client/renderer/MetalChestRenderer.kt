package io.github.trcdevelopers.clayium.client.renderer

import io.github.trcdevelopers.clayium.api.unification.material.CMaterial
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.blocks.metalchest.TileEntityMetalChest
import net.minecraft.client.model.ModelChest
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.EnumFacing
import org.lwjgl.opengl.GL11

object MetalChestRenderer : TileEntitySpecialRenderer<TileEntityMetalChest>() {

    private val base = clayiumId("textures/entity/metalchest/base.png")
    private val dark = clayiumId("textures/entity/metalchest/dark.png")
    private val light = clayiumId("textures/entity/metalchest/light.png")
    private val modelChest = ModelChest()

    override fun render(te: TileEntityMetalChest, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        this.render(te.facing, te.material, te.prevLidAngle, te.lidAngle, x, y, z, partialTicks, destroyStage, alpha)
    }

    fun render(facing: EnumFacing, material: CMaterial, prevLidAngle: Float, lidAngle: Float, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        GlStateManager.enableDepth()
        GlStateManager.depthFunc(GL11.GL_LEQUAL)
        GlStateManager.depthMask(true)

        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        GlStateManager.color(1f, 1f, 1f, alpha)
        run {
            GlStateManager.translate(x, y + 1, z + 1)
            GlStateManager.scale(1f, -1f, -1f)
            GlStateManager.translate(0.5, 0.5, 0.5)

            val degrees = when (facing) {
                EnumFacing.DOWN, EnumFacing.UP -> 0f
                EnumFacing.NORTH -> 180f
                EnumFacing.SOUTH -> 0f
                EnumFacing.WEST -> 90f
                EnumFacing.EAST -> -90f
            }
            GlStateManager.rotate(degrees, 0f, 1f, 0f)
            GlStateManager.translate(-0.5, -0.5, -0.5)

            var f = prevLidAngle + (lidAngle - prevLidAngle) * partialTicks
            f = 1.0f - f
            f = 1.0f - f * f * f

            modelChest.chestLid.rotateAngleX = -(f * (Math.PI.toFloat() / 2f))

            if (destroyStage >= 0) {
                this.bindTexture(DESTROY_STAGES[destroyStage])
                GlStateManager.matrixMode(GL11.GL_TEXTURE)
                GlStateManager.pushMatrix()
                GlStateManager.scale(4.0f, 4.0f, 1.0f)
                GlStateManager.translate(0.0625f, 0.0625f, 0.0625f)
                GlStateManager.matrixMode(GL11.GL_MODELVIEW)

                modelChest.renderAll()

                GlStateManager.matrixMode(GL11.GL_TEXTURE)
                GlStateManager.popMatrix()
                GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            } else {
                GlStateManager.disableBlend()
                val colors = material.colors ?: intArrayOf(0xFFFFFF, 0xFFFFFF, 0xFFFFFF)
                this.bindTexture(base)
                this.glColorRGB(colors[0], alpha)
                modelChest.renderAll()

                GlStateManager.enableBlend()
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GlStateManager.depthMask(false)

                GlStateManager.enablePolygonOffset()
                GlStateManager.doPolygonOffset(-0.1f, -1.0f)
                this.bindTexture(dark)
                this.glColorRGB(colors[1], alpha)
                modelChest.renderAll()

                GlStateManager.doPolygonOffset(-0.2f, -2.0f)
                this.bindTexture(light)
                this.glColorRGB(colors[2], alpha)
                modelChest.renderAll()

                GlStateManager.depthMask(true)
                GlStateManager.disablePolygonOffset()
                GlStateManager.enableAlpha()
                GlStateManager.disableBlend()
            }
        }
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
    }

    private fun glColorRGB(color: Int, alpha: Float) {
        GlStateManager.color(
            (color shr 16 and 0xFF) / 255.0f,
            (color shr 8 and 0xFF) / 255.0f,
            (color and 0xFF) / 255.0f,
            alpha
        )
    }
}