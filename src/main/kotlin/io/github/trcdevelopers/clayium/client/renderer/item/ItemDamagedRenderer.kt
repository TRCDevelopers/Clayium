package io.github.trcdevelopers.clayium.client.renderer.item

import codechicken.lib.render.CCRenderState
import codechicken.lib.render.item.IItemRenderer
import codechicken.lib.util.TransformUtils
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelSkeletonHead
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIXED
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GROUND
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GUI
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.HEAD
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.NONE
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.model.IModelState
import org.lwjgl.opengl.GL11


private val INGOT_RLS = listOf(
    clayiumId("textures/items/ingot_base"),
    clayiumId("textures/items/ingot_dark"),
    clayiumId("textures/items/ingot_light"),
)

object ItemDamagedRenderer : IItemRenderer {

    private val skeletonHead = ModelSkeletonHead(0, 0, 64, 32)
    private val WITHER_SKELETON_TEXTURES: ResourceLocation = ResourceLocation("textures/entity/skeleton/wither_skeleton.png")

    override fun renderItem(stack: ItemStack, transformType: ItemCameraTransforms.TransformType) {
        GlStateManager.pushMatrix()

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


                for (i in 0..<3) {
                    Minecraft.getMinecraft().textureManager.bindTexture(INGOT_RLS[i])
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
//                    GlStateManager.scale(-8.0F, -8.0F, 8.0F);

                    val model = Minecraft.getMinecraft().renderItem.itemModelMesher.getItemModel(stack)
                        ?: return
                    renderState.reset()
                    renderState.renderQuads(model.getQuads(null, null, 0))
                    renderState.draw()

                    GlStateManager.enableLighting()
                    GlStateManager.disableAlpha()
                }


                GlStateManager.disableBlend()
            }
            GROUND -> {}
            FIXED -> {}
        }
        GlStateManager.popMatrix()
    }

    override fun getTransforms(): IModelState {
        return TransformUtils.DEFAULT_ITEM
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true
}