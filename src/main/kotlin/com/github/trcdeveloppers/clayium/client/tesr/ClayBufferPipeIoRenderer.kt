package com.github.trcdeveloppers.clayium.client.tesr

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.client.ModelUtils
import com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer.TileClayBuffer
import com.github.trcdeveloppers.clayium.common.items.ClayiumItems
import net.minecraft.client.model.PositionTextureVertex
import net.minecraft.client.model.TexturedQuad
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

object ClayBufferPipeIoRenderer : TileEntitySpecialRenderer<TileClayBuffer>() {

    // offset to prevent z-fighting
    private const val CUBE_OFFSET = 0.01f

    override fun render(
        te: TileClayBuffer,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        val player = this.rendererDispatcher.entity as? EntityPlayer ?: return
        if (!isPipingTool(player.heldItemMainhand.item)) return

        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)

        this.bindTexture(ResourceLocation(Clayium.MOD_ID, "textures/blocks/import.png"))

        val buf = Tessellator.getInstance().buffer
        val connections = te.connections

        for (side in EnumFacing.entries) {
            if (!connections[side.index]) continue
            for (quad in createQuadsFor(side)) {
                quad.draw(buf, 0.0625f)
            }
        }

        GlStateManager.popMatrix()
    }

    private fun isPipingTool(item: Item): Boolean {
        return item == ClayiumItems.CLAY_PIPING_TOOL || item == ClayiumItems.CLAY_IO_CONFIGURATOR
    }

    private fun createQuadsFor(cubePos: EnumFacing): List<TexturedQuad> {
        val (x, y, z) = when (cubePos) {
            EnumFacing.DOWN -> Triple(5f - CUBE_OFFSET, 0f - CUBE_OFFSET, 5f - CUBE_OFFSET)
            EnumFacing.UP -> Triple(5f - CUBE_OFFSET, 11f - CUBE_OFFSET, 5f - CUBE_OFFSET)
            EnumFacing.NORTH -> Triple(5f - CUBE_OFFSET, 5f - CUBE_OFFSET, 0f - CUBE_OFFSET)
            EnumFacing.SOUTH -> Triple(5f - CUBE_OFFSET, 5f - CUBE_OFFSET, 11f - CUBE_OFFSET)
            EnumFacing.WEST -> Triple(0f - CUBE_OFFSET, 5f - CUBE_OFFSET, 5f - CUBE_OFFSET)
            EnumFacing.EAST -> Triple(11f - CUBE_OFFSET, 5f - CUBE_OFFSET, 5f - CUBE_OFFSET)
        }

        val xTo = x + (if (cubePos.axis == EnumFacing.Axis.X) 5f else 6f) + CUBE_OFFSET * 2
        val yTo = y + (if (cubePos.axis == EnumFacing.Axis.Y) 5f else 6f) + CUBE_OFFSET * 2
        val zTo = z + (if (cubePos.axis == EnumFacing.Axis.Z) 5f else 6f) + CUBE_OFFSET * 2

        val ptv7 = PositionTextureVertex(x, y, z, 0.0f, 0.0f)
        val ptv0 = PositionTextureVertex(xTo, y, z, 0.0f, 8.0f)
        val ptv1 = PositionTextureVertex(xTo, yTo, z, 8.0f, 8.0f)
        val ptv2 = PositionTextureVertex(x, yTo, z, 8.0f, 0.0f)
        val ptv3 = PositionTextureVertex(x, y, zTo, 0.0f, 0.0f)
        val ptv4 = PositionTextureVertex(xTo, y, zTo, 0.0f, 8.0f)
        val ptv5 = PositionTextureVertex(xTo, yTo, zTo, 8.0f, 8.0f)
        val ptv6 = PositionTextureVertex(x, yTo, zTo, 8.0f, 0.0f)

        val quads = mutableListOf<TexturedQuad>()

        for (facingOfQuad in EnumFacing.entries) {
            if (facingOfQuad.axis == cubePos.axis) continue
            val ptvs = when (facingOfQuad) {
                EnumFacing.DOWN -> arrayOf(ptv4, ptv3, ptv7, ptv0)
                EnumFacing.UP -> arrayOf(ptv1, ptv2, ptv6, ptv5)
                EnumFacing.NORTH -> arrayOf(ptv2, ptv1, ptv0, ptv7)
                EnumFacing.SOUTH -> arrayOf(ptv5, ptv6, ptv3, ptv4)
                EnumFacing.EAST -> arrayOf(ptv1, ptv5, ptv4, ptv0)
                EnumFacing.WEST -> arrayOf(ptv6, ptv2, ptv7, ptv3)
            }
            val uv = ModelUtils.getUvInt(cubePos, facingOfQuad)
            quads.add(TexturedQuad(ptvs, uv[0], uv[1], uv[2], uv[3], 16f, 16f))
        }
        return quads
    }
}