package com.github.trc.clayium.client.renderer

import com.github.trc.clayium.api.capability.ISynchronizedInterface
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.DimensionManager
import org.lwjgl.opengl.GL11
import kotlin.math.sin

object InterfaceRenderer {

    val OVERLAY_AABB = Block.FULL_BLOCK_AABB.grow(0.001)

    fun renderHighlight(
        tileEntity: MetaTileEntityHolder, syncInterface: ISynchronizedInterface, x: Double, y: Double, z: Double,
        partialTicks: Float,
    ) {
        val metaTileEntityStack = syncInterface.targetItemStack
        if (metaTileEntityStack.isEmpty) return

        val targetPos = syncInterface.targetPos
        val targetDimensionId = syncInterface.targetDimensionId
        if (targetPos == null) return
        val targetDimensionType = DimensionManager.getProviderType(targetDimensionId)
        val mc = Minecraft.getMinecraft()
        val raytraceResult = mc.objectMouseOver ?: return
        val isBlockSelected = raytraceResult.blockPos == tileEntity.pos
        if (!isBlockSelected) return

        val tickTime = (mc.world.totalWorldTime) + partialTicks

        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableDepth()
        GlStateManager.depthMask(false)

        GlStateManager.pushMatrix()
        run {
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5)
            when (raytraceResult.sideHit) {
                EnumFacing.DOWN -> GlStateManager.translate(0f, -0.8f, 0f)
                EnumFacing.UP -> GlStateManager.translate(0f, 0.8f, 0f)
                EnumFacing.NORTH -> GlStateManager.translate(0f, 0f, -0.8f)
                EnumFacing.SOUTH -> GlStateManager.translate(0f, 0f, 0.8f)
                EnumFacing.WEST -> GlStateManager.translate(-0.8f, 0f, 0f)
                EnumFacing.EAST -> GlStateManager.translate(0.8f, 0f, 0f)
            }
            renderItem(metaTileEntityStack, tickTime.toFloat())
            GlStateManager.pushMatrix()
                GlStateManager.translate(0f, 0.4f, 0f)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
                renderString(metaTileEntityStack.displayName)
            GlStateManager.popMatrix()

            GlStateManager.pushMatrix()
                GlStateManager.translate(0f, 0.275f, 0f)
                GlStateManager.scale(0.25f, 0.25f, 0.25f)
            @Suppress("UsePropertyAccessSyntax") // .getName() instead of .name for lower-case
            renderString("${targetPos.x}, ${targetPos.y}, ${targetPos.z}; ${targetDimensionType.getName()}")
            GlStateManager.popMatrix()
        }
        GlStateManager.popMatrix()
        GlStateManager.pushMatrix()
        run {
            if (targetDimensionId == tileEntity.world?.provider?.dimension) {
                val offsetPos = targetPos.subtract(tileEntity.pos)

                val tessellator = Tessellator.getInstance()
                val bufferBuilder = tessellator.buffer

                val red = (sin(tickTime * 0.1) + 1.0) * 0.5
                val green = (sin(tickTime * 0.1 + 2.1) + 1.0) * 0.5
                val blue = (sin(tickTime * 0.1 + 4.2) + 1.0) * 0.5
                GlStateManager.color(red.toFloat(), green.toFloat(), blue.toFloat(), 0.25f)

                GlStateManager.translate(x, y, z)
                GlStateManager.disableTexture2D()
                GlStateManager.disableLighting()
                GlStateManager.disableCull()
                GlStateManager.disableDepth()
                bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
                GlStateManager.glLineWidth(4f)
                bufferBuilder.pos(0.5, 0.5, 0.5).endVertex()
                bufferBuilder.pos(offsetPos.x + 0.5, offsetPos.y + 0.5, offsetPos.z + 0.5).endVertex()
                tessellator.draw()
                GlStateManager.translate(offsetPos.x.toDouble(), offsetPos.y.toDouble(), offsetPos.z.toDouble())
                // render full block box
                val aabb = OVERLAY_AABB
                bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL)
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).normal(0.0F, 0.0F, -1.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).normal(0.0F, 0.0F, -1.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).normal(0.0F, 0.0F, -1.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).normal(0.0F, 0.0F, -1.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).normal(0.0F, -1.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).normal(0.0F, -1.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).normal(0.0F, 1.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).normal(0.0F, 1.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).normal(1.0F, 0.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).normal(1.0F, 0.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex()
                tessellator.draw()
                GlStateManager.enableDepth()
                GlStateManager.enableCull()
                GlStateManager.enableLighting()
                GlStateManager.enableTexture2D()
                GlStateManager.color(1f, 1f, 1f, 1f)
            }
        }
        GlStateManager.popMatrix()

        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
    }

    private fun renderItem(stack: ItemStack, tickTime: Float) {
        GlStateManager.pushMatrix()
        run {
            val mc = Minecraft.getMinecraft()
            GlStateManager.translate(0f, 0.05f, 0f)
            GlStateManager.scale(0.25f, 0.25f, 0.25f)
            GlStateManager.color(1f, 1f, 1f, 0.7f)
            val itemBakedModel = mc.renderItem.getItemModelWithOverrides(stack, null, null)
            mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            mc.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false)
            GlStateManager.pushMatrix()

            GlStateManager.rotate(tickTime * 4f, 0f, 1f, 0f)
            mc.renderItem.renderItem(stack, itemBakedModel)
            GlStateManager.popMatrix()
            mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            mc.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap()
        }
        GlStateManager.popMatrix()
    }

    private fun renderString(text: String) {
        val mc = Minecraft.getMinecraft()
        GlStateManager.pushMatrix()
        run {
            GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f)
            GlStateManager.scale(-0.025f, -0.025f, 0.025f)
            GlStateManager.depthMask(false)

            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
            )
            val width = mc.fontRenderer.getStringWidth(text) / 2

            GlStateManager.disableTexture2D()
            GlStateManager.color(0f, 0f, 0f, 0.5f)
            val player = mc.player
            GlStateManager.rotate(player.rotationYaw, 0f, 1f, 0f)
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

            Minecraft.getMinecraft().fontRenderer.drawString(text, -width, 0, 0xFFFFFF)

            GlStateManager.depthMask(false)
            GlStateManager.color(1f, 1f, 1f, 1f)
        }
        GlStateManager.popMatrix()
    }
}