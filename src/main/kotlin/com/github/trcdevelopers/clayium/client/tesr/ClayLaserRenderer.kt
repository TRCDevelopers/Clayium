package com.github.trcdevelopers.clayium.client.tesr

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.block.BlockMachine
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trcdevelopers.clayium.common.config.ConfigCore
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.ResourceLocation

object ClayLaserRenderer : TileEntitySpecialRenderer<MetaTileEntityHolder>() {
    override fun render(holder: MetaTileEntityHolder, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        if (holder.blockType !is BlockMachine) return
        val metaTileEntity = holder.metaTileEntity ?: return
        val clayLaser = metaTileEntity.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER, null) ?: return

        GlStateManager.pushMatrix();
        {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.75f)
            GlStateManager.translate(x + 0.5f, y + 0.5f, z + 0.5f)
            when (clayLaser.laserDirection) {
                DOWN -> GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f)
                UP -> GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f)
                NORTH -> GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
                SOUTH -> GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f)
                WEST -> GlStateManager.rotate(-90.0f, 0.0f, 0.0f, -1.0f)
                EAST -> GlStateManager.rotate(90.0f, 0.0f, 0.0f, -1.0f)
            }

            val laserRgb = clayLaser.getLaserRgb()
            val red = (laserRgb shr 16)
            val green = (laserRgb shr 8 and 0xFF)
            val blue = (laserRgb and 0xFF)

            val str = laserRgb
            val max = maxOf(red, green, blue)
            val scale = 1.0f - 14.0625f / (str + 14)

            GlStateManager.scale(scale, 1.0f, scale)
            GlStateManager.translate(0.0f, -scale / 6.0f, 0.0f)
            GlStateManager.scale(1.0f, clayLaser.getLaserLength() + scale / 3.0f, 1.0f)

            bindTexture(ResourceLocation(CValues.MOD_ID, "textures/blocks/laser.png"))
            val f14 = 0.0F;
            val f15 = 1.0F;
            val f4 = 0.0F;
            val f5 = 1.0F;

            val tessellator = Tessellator.getInstance()
            val buffer = tessellator.buffer

            val laserQuality = ConfigCore.rendering.laserQuality
            for (i in 0..laserQuality) {
                buffer.color(red, green, blue, ((26.0f + scale * 26.0f) * 8 / laserQuality).toInt());
//                buffer.addVertexData();
//                tessellator.addVertexWithUV(0.0D, 1.0D, 0.5D, f14, f4);
//                tessellator.addVertexWithUV(0.0D, 1.0D, -0.5D, f15, f4);
//                tessellator.addVertexWithUV(0.0D, 0.0D, -0.5D, f15, f5);
//                tessellator.addVertexWithUV(0.0D, 0.0D, 0.5D, f14, f5);
                tessellator.draw();
                GlStateManager.rotate(180.0f / laserQuality, 0.0f, 1.0f, 0.0f);
            }
        }
        GlStateManager.popMatrix()
    }
}