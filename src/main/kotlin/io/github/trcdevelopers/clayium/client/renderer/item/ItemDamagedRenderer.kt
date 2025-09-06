package io.github.trcdevelopers.clayium.client.renderer.item

import codechicken.lib.render.item.IItemRenderer
import codechicken.lib.util.TransformUtils
import io.github.trcdevelopers.clayium.api.util.clayiumId
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.model.pipeline.LightUtil
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.model.TRSRTransformation
import org.lwjgl.opengl.GL11


object ItemDamagedRenderer : IItemRenderer {

    private val ingotL0Mrl = ModelResourceLocation(clayiumId("colored/ingot_l0"), "inventory")
    private val ingotL1Mrl = ModelResourceLocation(clayiumId("colored/ingot_l1"), "inventory")
    private val ingotL2Mrl = ModelResourceLocation(clayiumId("colored/ingot_l2"), "inventory")

    private lateinit var modelL0: IBakedModel
    private lateinit var modelL1: IBakedModel
    private lateinit var modelL2: IBakedModel

    fun init() {
        val modelManager = Minecraft.getMinecraft().renderItem.itemModelMesher.modelManager
        modelL0 = modelManager.getModel(ingotL0Mrl)
        modelL1 = modelManager.getModel(ingotL1Mrl)
        modelL2 = modelManager.getModel(ingotL2Mrl)
    }

    override fun renderItem(stack: ItemStack, transformType: ItemCameraTransforms.TransformType) {
        val mc = Minecraft.getMinecraft()
        val tessellator = Tessellator.getInstance()
        val buf = tessellator.buffer

        mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        mc.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableRescaleNormal()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        )

        GlStateManager.pushMatrix()

        // L0
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)
        renderModelQuads(buf, modelL0.getQuads(null, null, 0L), stack, 0)
        for (facing in EnumFacing.entries) {
            renderModelQuads(buf, modelL0.getQuads(null, facing, 0L), stack, 0)
        }
        tessellator.draw()

        GlStateManager.enablePolygonOffset()
        GlStateManager.doPolygonOffset(-1.0f, -10.0f)

        // L1,L2
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)
        renderModelQuads(buf, modelL1.getQuads(null, null, 0L), stack, 1)
        for (facing in EnumFacing.entries) {
            renderModelQuads(buf, modelL1.getQuads(null, facing, 0L), stack, 1)
        }

        renderModelQuads(buf, modelL2.getQuads(null, null, 0L), stack, 2)
        for (facing in EnumFacing.entries) {
            renderModelQuads(buf, modelL2.getQuads(null, facing, 0L), stack, 2)
        }

        tessellator.draw()

        GlStateManager.disablePolygonOffset()

        GlStateManager.cullFace(GlStateManager.CullFace.BACK)
        GlStateManager.popMatrix()

        GlStateManager.disableRescaleNormal()
        GlStateManager.disableBlend()
        mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        mc.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap()
    }

    override fun getTransforms(): IModelState {
        return TransformUtils.DEFAULT_ITEM
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true

    private fun renderModelQuads(buf: BufferBuilder, quads: List<BakedQuad>, stack: ItemStack, i: Int) {
        val flag = !stack.isEmpty

        for (bakedquad in quads) {
            var k: Int = -1
            if (flag && bakedquad.hasTintIndex()) {
                k = Minecraft.getMinecraft().itemColors.colorMultiplier(stack, i)

                if (EntityRenderer.anaglyphEnable) {
                    k = TextureUtil.anaglyphColor(k)
                }

                k = k or -16777216
            }
            LightUtil.renderQuadColor(buf, bakedquad, k)
        }
    }
}