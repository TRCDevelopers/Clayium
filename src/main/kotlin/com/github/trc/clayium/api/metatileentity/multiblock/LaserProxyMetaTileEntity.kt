package com.github.trc.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserAcceptor
import com.github.trc.clayium.api.capability.IConfigurationTool
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.laser.IClayLaser
import com.github.trc.clayium.api.metatileentity.AutoIoHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils.clayiumId
import com.github.trc.clayium.api.util.ITier
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class LaserProxyMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, "machine.${CValues.MOD_ID}.laser_proxy"), IClayLaserAcceptor {

    private var laser: IClayLaser? = null
    override val faceTexture: ResourceLocation = clayiumId("blocks/laserinterface")

    override val importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val exportItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val itemInventory: IItemHandler = EmptyItemStackHandler
    override val autoIoHandler: AutoIoHandler = AutoIoHandler.Empty(this)

    override fun isFacingValid(facing: EnumFacing) = true

    override fun onLink(target: MetaTileEntity) {
        super.onLink(target)
        if (this.laser != null) {
            target.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, this.frontFacing.opposite)
                ?.laserChanged(this.frontFacing.opposite, this.laser)
        }
    }

    override fun onUnlink() {
        super.onUnlink()
        this.target?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, this.frontFacing.opposite)
            ?.laserChanged(this.frontFacing.opposite, null)
    }

    override fun canLink(target: MetaTileEntity): Boolean {
        return super.canLink(target) && target.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, this.frontFacing.opposite) != null
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return LaserProxyMetaTileEntity(metaTileEntityId, tier)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("laser_proxy"), "tier=${tier.lowerName}"))
    }

    override fun canOpenGui() = false
    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): Nothing {
        throw UnsupportedOperationException()
    }

    override fun laserChanged(irradiatedSide: EnumFacing, laser: IClayLaser?) {
        println("Laser changed: $laser")
        if (irradiatedSide == this.frontFacing) {
            this.laser = laser
            this.target?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, this.frontFacing.opposite)
                ?.laserChanged(this.frontFacing.opposite, laser)
        }
    }

    override fun onToolClick(toolType: IConfigurationTool.ToolType, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        super.onToolClick(toolType, player, hand, clickedSide, hitX, hitY, hitZ)
        if (laser?.laserDirection != this.frontFacing.opposite) {
            this.laser = null
        }
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR && facing == this.frontFacing) {
            return ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR.cast(this)
        }
        return super.getCapability(capability, facing)
    }
}