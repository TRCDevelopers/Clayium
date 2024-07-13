package com.github.trc.clayium.client.renderer

import com.github.trc.clayium.api.block.BlockMachine
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.ISynchronizedInterface
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.player.EntityPlayer

object MetaTileEntityRenderDispatcher : TileEntitySpecialRenderer<MetaTileEntityHolder>() {
    override fun render(holder: MetaTileEntityHolder, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        if (holder.blockType !is BlockMachine) return
        val metaTileEntity = holder.metaTileEntity ?: return
        metaTileEntity.renderMetaTileEntity(x, y, z, partialTicks)
        val clayLaserManager = metaTileEntity.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER, null)
        if (clayLaserManager?.isActive == true) {
            ClayLaserRenderer.renderLaser(clayLaserManager, x, y, z, this::bindTexture)
        }
        if (metaTileEntity is ISynchronizedInterface) {
            InterfaceRenderer.renderHighlight(holder, metaTileEntity, x, y, z, partialTicks)
        }
        if (world.getBlockState(holder.pos).getValue(BlockMachine.IS_PIPE)) {
            PipedMachineIoRenderer.renderPipeIoIcons(holder, x, y, z, this.rendererDispatcher.entity as? EntityPlayer ?: return, this::bindTexture)
        }
    }

    override fun isGlobalRenderer(te: MetaTileEntityHolder): Boolean {
        return te.metaTileEntity?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER, null) != null
    }
}