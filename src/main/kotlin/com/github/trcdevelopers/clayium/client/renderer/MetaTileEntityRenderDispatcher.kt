package com.github.trcdevelopers.clayium.client.renderer

import com.github.trcdevelopers.clayium.api.block.BlockMachine
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

object MetaTileEntityRenderDispatcher : TileEntitySpecialRenderer<MetaTileEntityHolder>() {
    override fun render(holder: MetaTileEntityHolder, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        if (holder.blockType !is BlockMachine) return
        val metaTileEntity = holder.metaTileEntity ?: return
        val clayLaserManager = metaTileEntity.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER, null) ?: return
        if (clayLaserManager.isActive) {
            ClayLaserRenderer.renderLaser(clayLaserManager, x, y, z, this::bindTexture)
        }
        if (world.getBlockState(holder.pos).getValue(BlockMachine.IS_PIPE)) {
            PipedMachineIoRenderer.render(holder, x, y, z, partialTicks, destroyStage, alpha)
        }
    }
}