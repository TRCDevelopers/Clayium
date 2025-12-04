package io.github.trcdevelopers.clayium.client.renderer

import codechicken.lib.render.state.GlStateTracker
import io.github.trcdevelopers.clayium.api.capability.ISynchronizedInterface
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
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

        val mc = Minecraft.getMinecraft()
        val raytraceResult = mc.objectMouseOver ?: return
        val isBlockSelected = raytraceResult.blockPos == tileEntity.pos
        if (!isBlockSelected) return

        val targetPos = syncInterface.targetPos ?: return
        val targetDimensionId = syncInterface.targetDimensionId
        // .getName() instead of .name for lower-case
        val targetDimensionName = DimensionManager.getProviderType(targetDimensionId)?.getName() ?: return

        val tickTime = (mc.world.totalWorldTime) + partialTicks

        GlStateTracker.pushState()
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
            GlStateManager.disableLighting()
            renderItem(metaTileEntityStack, tickTime)
            GlStateManager.pushMatrix()
            run {
                GlStateManager.translate(0f, 0.4f, 0f)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
                renderString(metaTileEntityStack.displayName)
            }
            GlStateManager.popMatrix()

            GlStateManager.pushMatrix()
            run {
                GlStateManager.translate(0f, 0.275f, 0f)
                GlStateManager.scale(0.25f, 0.25f, 0.25f)
                renderString("${targetPos.x}, ${targetPos.y}, ${targetPos.z}; $targetDimensionName")
            }
            GlStateManager.popMatrix()
        }
        GlStateManager.popMatrix()
        // Draw a highlighting line and an overlay if in the same dimension
        GlStateManager.pushMatrix()
        CRenderUtils.enableTranslucent()
        CRenderUtils.enableXray()
        GlStateManager.disableTexture2D()
        run {
            if (targetDimensionId == tileEntity.world.provider?.dimension) {
                val offsetPos = targetPos.subtract(tileEntity.pos)

                val tessellator = Tessellator.getInstance()
                val bufferBuilder = tessellator.buffer

                val r = ((sin(tickTime * 0.1) + 1.0) * 0.5).toFloat()
                val g = ((sin(tickTime * 0.1 + 2.1) + 1.0) * 0.5).toFloat()
                val b = ((sin(tickTime * 0.1 + 4.2) + 1.0) * 0.5).toFloat()
                val a = 0.25f

                GlStateManager.color(r, g, b, a)
                GlStateManager.translate(x, y, z)

                bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
                GlStateManager.glLineWidth(4f)
                bufferBuilder.pos(0.5, 0.5, 0.5).endVertex()
                bufferBuilder.pos(offsetPos.x + 0.5, offsetPos.y + 0.5, offsetPos.z + 0.5).endVertex()
                tessellator.draw()
                GlStateManager.translate(offsetPos.x.toDouble(), offsetPos.y.toDouble(), offsetPos.z.toDouble())
                // render full block box
                val aabb = OVERLAY_AABB
                CRenderUtils.renderCube(aabb, r, g, b, a)
            }
        }
        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.popMatrix()
        GlStateTracker.popState()
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
        val player = mc.player
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f)
        GlStateManager.scale(-0.025f, -0.025f, 0.025f)
        GlStateManager.rotate(player.rotationYaw, 0f, 1f, 0f)
        CRenderUtils.renderStringWithBackground(text, 0xFFFFFF)
    }
}