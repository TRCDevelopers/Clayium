package com.github.trcdevelopers.clayium.client.tesr

import com.github.trcdevelopers.clayium.client.ModelUtils
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.ALL
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.CE
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.FIRST
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.M_1
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.M_2
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.M_3
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.M_4
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.M_5
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.M_6
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.M_ALL
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.NONE
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.SECOND
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileMachine
import com.github.trcdevelopers.clayium.common.items.ClayiumItems
import net.minecraft.client.model.PositionTextureVertex
import net.minecraft.client.model.TexturedQuad
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

object ClayBufferPipeIoRenderer : TileEntitySpecialRenderer<TileMachine>() {

    // offset to prevent z-fighting
    private const val CUBE_OFFSET = 0.01f

    private val inputTextures: Map<MachineIoMode, ResourceLocation?> = MachineIoMode.entries.associateWith {
        when (it) {
            NONE -> null
            FIRST -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_1_p.png")
            SECOND -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_2_p.png")
            ALL -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_p.png")
            CE -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_energy_p.png")
            M_ALL -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_m0_p.png")
            M_1 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_m1_p.png")
            M_2 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_m2_p.png")
            M_3 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_m3_p.png")
            M_4 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_m4_p.png")
            M_5 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/import_m5_p.png")
            M_6 -> ResourceLocation (Clayium.MOD_ID, "textures/blocks/import_m6_p.png")
        }
    }

    private val outputTextures: Map<MachineIoMode, ResourceLocation?> = MachineIoMode.entries.associateWith {
        when (it) {
            NONE, CE -> null
            FIRST -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/export_1_p.png")
            SECOND -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/export_2_p.png")
            ALL -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/export_p.png")
            M_ALL -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/export_m0_p.png")
            M_1 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/export_m1_p.png")
            M_2 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/export_m2_p.png")
            M_3 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/export_m3_p.png")
            M_4 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/export_m4_p.png")
            M_5 -> ResourceLocation(Clayium.MOD_ID, "textures/blocks/export_m5_p.png")
            M_6 -> ResourceLocation (Clayium.MOD_ID, "textures/blocks/export_m6_p.png")
        }
    }

    private val sideQuads = EnumFacing.entries.map { createQuadsFor(it) }

    override fun render(
        te: TileMachine, x: Double, y: Double, z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        if (te.blockType !is BlockMachine) return
        if (!world.getBlockState(te.pos).getValue(BlockMachine.IS_PIPE)) return

        val player = this.rendererDispatcher.entity as? EntityPlayer ?: return
        if (!isPipingTool(player.heldItemMainhand.item)) return

        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)

        val buf = Tessellator.getInstance().buffer

        val connections = te.connections
        val inputs = te.inputs
        val outputs = te.outputs

        for (side in EnumFacing.entries) {
            val i = side.index
            if (!connections[i]) continue

            val inputTex = inputTextures[inputs[i]]
            if (inputTex != null) {
                bindTexture(inputTex)
                sideQuads[i].forEach { it.draw(buf, 0.0625f) }
            }
            val outputTex = outputTextures[outputs[i]]
            if (outputTex != null) {
                bindTexture(outputTex)
                sideQuads[i].forEach { it.draw(buf, 0.0625f) }
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