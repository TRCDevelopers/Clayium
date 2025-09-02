package io.github.trcdevelopers.clayium.client.renderer.item

import codechicken.lib.render.CCRenderState
import codechicken.lib.render.item.IItemRenderer
import codechicken.lib.util.TransformUtils
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.*
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraftforge.common.model.IModelState
import org.lwjgl.opengl.GL11

private val INGOT_RLS = listOf(
    clayiumId("textures/items/ingot_base"),
    clayiumId("textures/items/ingot_dark"),
    clayiumId("textures/items/ingot_light"),
)

object ItemDamagedRenderer : IItemRenderer {
    override fun renderItem(stack: ItemStack, transformType: ItemCameraTransforms.TransformType) {
        when (transformType) {
            NONE -> {}
            THIRD_PERSON_LEFT_HAND -> {}
            THIRD_PERSON_RIGHT_HAND -> {}
            FIRST_PERSON_LEFT_HAND -> {}
            FIRST_PERSON_RIGHT_HAND -> {}
            HEAD -> {}
            GUI -> {
//        val itemColors = Minecraft.getMinecraft().itemColors
//        val color = itemColors.colorMultiplier(stack, 0)
                val item = stack.item as? MetaItemClayium ?: return
                val metaItem = item.getItem(stack) ?: return
                val colorHandler = metaItem.colorHandler ?: return
                val renderState = CCRenderState.instance()
                GlStateManager.enableBlend()

                for (i in 0..<3) {
                    val color = colorHandler.getColor(stack, i)
                    val r = (color shr 16 and 255).toFloat() / 255.0f
                    val g = (color shr 8 and 255).toFloat() / 255.0f
                    val b = (color and 255).toFloat() / 255.0f

                    GlStateManager.tryBlendFuncSeparate(
                        GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                        GL11.GL_ONE, GL11.GL_ZERO
                    )
                    GlStateManager.color(r, g, b, 1f)
                    GlStateManager.disableLighting()
                    GlStateManager.enableAlpha()

                    renderState.reset()
//                    renderState.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.ITEM)
                    renderState.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
                    Minecraft.getMinecraft().textureManager.bindTexture(INGOT_RLS[i])
                    val buf = CCRenderState.instance().buffer
//                    buf.pos(0.0, 16.0, 50.0).color(r, g, b, 1f).tex(0.0, 1.0).normal(0.0f, 0.0f, 1.0f).endVertex()
//                    buf.pos(16.0, 16.0, 50.0).color(r, g, b, 1f).tex(0.0, 1.0).normal(0.0f, 0.0f, 1.0f).endVertex()
//                    buf.pos(16.0, 0.0, 50.0).color(r, g, b, 1f).tex(0.0, 1.0).normal(0.0f, 0.0f, 1.0f).endVertex()
//                    buf.pos(0.0, 0.0, 50.0).color(r, g, b, 1f).tex(0.0, 1.0).normal(0.0f, 0.0f, 1.0f).endVertex()
                    buf.pos(0.0, 1.0, 0.0).tex(0.0, 1.0).color(r, g, b, 1f).endVertex()
                    buf.pos(1.0, 1.0, 0.0).tex(1.0, 1.0).color(r, g, b, 1f).endVertex()
                    buf.pos(1.0, 0.0, 0.0).tex(1.0, 0.0).color(r, g, b, 1f).endVertex()
                    buf.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(r, g, b, 1f).endVertex()
                    renderState.draw()

                    GlStateManager.enableLighting()
                    GlStateManager.disableAlpha()
                }


                GlStateManager.disableBlend()
            }
            GROUND -> {}
            FIXED -> {}
        }
    }

    override fun getTransforms(): IModelState {
        return TransformUtils.DEFAULT_ITEM
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
}