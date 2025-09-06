package io.github.trcdevelopers.clayium.client.renderer.item

import codechicken.lib.render.item.IItemRenderer
import codechicken.lib.util.TransformUtils
import io.github.trcdevelopers.clayium.api.util.CLog
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.client.renderer.CRenderUtils
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.pipeline.LightUtil
import net.minecraftforge.common.model.IModelState
import org.lwjgl.opengl.GL11


class ItemDamagedRenderer(
    val map: Short2ObjectMap<List<ModelResourceLocation>>,
) : IItemRenderer {

    /**
     * @param base The base name of the item model, without the "colored/" prefix and "_lX" suffix. namespace is always "clayium".
     */
    constructor(base: String): this(
        Short2ObjectOpenHashMap<List<ModelResourceLocation>>().apply {
            defaultReturnValue(
                listOf(
                    ModelResourceLocation(clayiumId("colored/${base}_l0"), "inventory"),
                    ModelResourceLocation(clayiumId("colored/${base}_l1"), "inventory"),
                    ModelResourceLocation(clayiumId("colored/${base}_l2"), "inventory"),
                )
            )
        }
    )

    init {
        renderers.add(this)

        for ((meta, models) in map) {
            if (models.size != 3) {
                CLog.warn(
                    "ItemDamagedRenderer requires exactly 3 models for each metadata, but got {} for metadata {} in map {}",
                    models.size, meta, map
                )
            }
        }
    }

    private val models = Short2ObjectOpenHashMap<List<IBakedModel>>()

    fun init() {
        val default = map.defaultReturnValue()
        val modelManager = Minecraft.getMinecraft().renderItem.itemModelMesher.modelManager
        if (default != null) {
            models.defaultReturnValue(
                default.map { modelManager.getModel(it) }
            )
        }
        for ((k, v) in map) {
            models.put(k, v.map { modelManager.getModel(it) })
        }
    }

    fun getAllModelResourceLocations(): Set<ModelResourceLocation> {
        val set = mutableSetOf<ModelResourceLocation>()
        for (v in map.values) {
            set.addAll(v)
        }
        val defaultValue = map.defaultReturnValue()
        if (defaultValue != null) {
            set.addAll(defaultValue)
        }
        return set
    }

    override fun renderItem(stack: ItemStack, transformType: ItemCameraTransforms.TransformType) {

        val meta = stack.metadata.toShort()
        val models = models.get(meta)
        if (models == null || models.size != 3) {
            return
        }
        val (modelL0, modelL1, modelL2) = models

        val mc = Minecraft.getMinecraft()
        val tessellator = Tessellator.getInstance()
        val buf = tessellator.buffer

        mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        mc.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false)
        val states = CRenderUtils.memoryCurrentStates()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableRescaleNormal()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        )

        if (transformType == ItemCameraTransforms.TransformType.GUI) {
            GlStateManager.disableLighting()
        }

        GlStateManager.pushMatrix()

        // L0
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)
        renderModelQuads(buf, modelL0.getQuads(null, null, 0L), stack, 0)
        for (facing in EnumFacing.entries) {
            renderModelQuads(buf, modelL0.getQuads(null, facing, 0L), stack, 0)
        }
        tessellator.draw()

        GlStateManager.enablePolygonOffset()
        GlStateManager.doPolygonOffset(-0.01f, -0.1f)

        // L1,L2
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)
        renderModelQuads(buf, modelL1.getQuads(null, null, 0L), stack, 1)
        for (facing in EnumFacing.entries) {
            renderModelQuads(buf, modelL1.getQuads(null, facing, 0L), stack, 1)
        }
        tessellator.draw()

        GlStateManager.doPolygonOffset(-0.02f, -0.2f)

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)
        renderModelQuads(buf, modelL2.getQuads(null, null, 0L), stack, 2)
        for (facing in EnumFacing.entries) {
            renderModelQuads(buf, modelL2.getQuads(null, facing, 0L), stack, 2)
        }

        tessellator.draw()

        GlStateManager.disablePolygonOffset()

        GlStateManager.cullFace(GlStateManager.CullFace.BACK)
        GlStateManager.popMatrix()

        GlStateManager.disableRescaleNormal()
        CRenderUtils.restoreStates(states)
        mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        mc.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap()
    }

    override fun getTransforms(): IModelState {
        return TransformUtils.DEFAULT_ITEM
    }

    override fun isAmbientOcclusion() = false
    override fun isGui3d() = true

    private fun renderModelQuads(buf: BufferBuilder, quads: List<BakedQuad>, stack: ItemStack, i: Int) {
        val flag = !stack.isEmpty
        var color: Int

        for (bakedquad in quads) {
            if (flag && bakedquad.hasTintIndex()) {
                var k = Minecraft.getMinecraft().itemColors.colorMultiplier(stack, i)

                if (EntityRenderer.anaglyphEnable) {
                    k = TextureUtil.anaglyphColor(k)
                }

                color = k or 0xFF000000.toInt()
            } else {
                color = -1
            }
            LightUtil.renderQuadColor(buf, bakedquad, color)
        }
    }

    companion object {
        private val renderers = mutableListOf<ItemDamagedRenderer>()
        fun init() {
            for (renderer in renderers) {
                renderer.init()
            }
        }

        fun getAllModelResourceLocations(): Set<ModelResourceLocation> {
            val set = mutableSetOf<ModelResourceLocation>()
            for (renderer in renderers) {
                set.addAll(renderer.getAllModelResourceLocations())
            }
            return set
        }
    }
}