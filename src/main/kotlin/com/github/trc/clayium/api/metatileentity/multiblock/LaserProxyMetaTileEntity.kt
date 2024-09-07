package com.github.trc.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserAcceptor
import com.github.trc.clayium.api.capability.IConfigurationTool
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class LaserProxyMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, "laser_proxy"), IClayLaserAcceptor {

    private var laser: ClayLaser? = null
    override val faceTexture: ResourceLocation = clayiumId("blocks/laser_proxy")

    override val importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val exportItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val itemInventory: IItemHandler = EmptyItemStackHandler

    override fun isFacingValid(facing: EnumFacing) = true

    override fun onLink(target: MetaTileEntity) {
        super.onLink(target)
        if (this.laser != null) {
            target.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, this.frontFacing.opposite)
                ?.acceptLaser(this.frontFacing.opposite, this.laser)
        }
    }

    override fun onUnlink() {
        super.onUnlink()
        this.target?.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, this.frontFacing.opposite)
            ?.acceptLaser(this.frontFacing.opposite, null)
    }

    override fun canLink(target: MetaTileEntity): Boolean {
        return super.canLink(target) && target.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, this.frontFacing.opposite) != null
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return LaserProxyMetaTileEntity(metaTileEntityId, tier)
    }

    override fun canOpenGui() = false
    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): Nothing {
        throw UnsupportedOperationException()
    }

    override fun acceptLaser(irradiatedSide: EnumFacing, laser: ClayLaser?) {
        if (irradiatedSide == this.frontFacing) {
            this.laser = laser
            this.target?.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, this.frontFacing.opposite)
                ?.acceptLaser(this.frontFacing.opposite, laser)
        }
    }

    override fun onToolClick(toolType: IConfigurationTool.ToolType, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        super.onToolClick(toolType, player, hand, clickedSide, hitX, hitY, hitZ)
        //todo onFrontFacing set
        this.laser = null
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR && facing == this.frontFacing) {
            return ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR.cast(this)
        }
        return super.getCapability(capability, facing)
    }
}