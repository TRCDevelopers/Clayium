package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.IClayLaserAcceptor
import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import com.github.trcdevelopers.clayium.api.metatileentity.AutoIoHandler
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler

class LaserProxyMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, onlyNoneList, onlyNoneList, "machine.${CValues.MOD_ID}.laser_proxy"), IClayLaserAcceptor {

    private var laser: IClayLaser? = null

    override val importItems: IItemHandlerModifiable = ItemStackHandler(0)
    override val exportItems: IItemHandlerModifiable = ItemStackHandler(0)
    override val itemInventory: IItemHandler = ItemStackHandler(0)
    override val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    override fun onLink(target: MetaTileEntity) {
        if (this.laser != null) {
            target.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, this.frontFacing.opposite)
                ?.laserChanged(this.frontFacing.opposite, this.laser)
        }
    }

    override fun onUnlink() {
        this.target?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, this.frontFacing.opposite)
            ?.laserChanged(this.frontFacing.opposite, null)
    }

    override fun canLink(target: MetaTileEntity): Boolean {
        return target.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, this.frontFacing.opposite) != null
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
        if (irradiatedSide == this.frontFacing) {
            this.laser = laser
            this.target?.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR, this.frontFacing.opposite)
                ?.laserChanged(this.frontFacing.opposite, laser)
        }
    }

    override fun onToolClick(toolType: ItemClayConfigTool.ToolType, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        super.onToolClick(toolType, player, hand, clickedSide, hitX, hitY, hitZ)
        if (laser?.laserDirection != this.frontFacing.opposite) {
            this.laser = null
        }
    }
}