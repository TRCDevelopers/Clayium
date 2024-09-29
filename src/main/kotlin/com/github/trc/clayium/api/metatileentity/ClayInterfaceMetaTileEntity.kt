package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.gui.MetaTileEntityGuiFactory
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.multiblock.ProxyMetaTileEntityBase
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class ClayInterfaceMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, "clay_interface") {

    override val faceTexture = clayiumId("blocks/clay_interface")
    override val useFaceForAllSides = true

    override var importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override var exportItems: IItemHandlerModifiable = EmptyItemStackHandler
    override var itemInventory: IItemHandler = EmptyItemStackHandler

    private var ecImporter: AutoIoHandler.EcImporter? = null
    private var autoIoHandler: AutoIoHandler? = null

    override var validInputModes: List<MachineIoMode> = onlyNoneList
    override var validOutputModes: List<MachineIoMode> = onlyNoneList

    var hasSynchroParts = false
        private set

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setBoolean("hasSynchroParts", hasSynchroParts)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        hasSynchroParts = data.getBoolean("hasSynchroParts")
    }

    override fun canSynchronize(): Boolean {
        return hasSynchroParts
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayInterfaceMetaTileEntity(metaTileEntityId, tier)
    }

    override fun onLink(target: MetaTileEntity) {
        super.onLink(target)
        this.importItems = target.importItems
        this.exportItems = target.exportItems
        this.itemInventory = ItemHandlerProxy(target.importItems, target.exportItems)
        target.getCapability(ClayiumTileCapabilities.AUTO_IO_HANDLER, null)?.let { targetHandler ->
            this.autoIoHandler = AutoIoHandler.Combined(this, targetHandler.isBuffer, target.tier.numeric)
        }
        target.getCapability(ClayiumTileCapabilities.CLAY_ENERGY_HOLDER, null)?.let { targetEnergyHolder ->
            this.ecImporter = AutoIoHandler.EcImporter(this, targetEnergyHolder.energizedClayItemHandler)
        }

        // Disable Trait Sync. These traits not exist on the client side, and they have no data to sync.
        val autoIoHandler = this.autoIoHandler
        if (autoIoHandler != null) {
            traitByNetworkId.remove(autoIoHandler.networkId)
        }
        val ecImporter = this.ecImporter
        if (ecImporter != null) {
            traitByNetworkId.remove(ecImporter.networkId)
        }

        this.validInputModes = target.validInputModes
        this.validOutputModes = target.validOutputModes
    }

    override fun onUnlink() {
        super.onUnlink()
        this.importItems = EmptyItemStackHandler
        this.exportItems = EmptyItemStackHandler
        this.itemInventory = EmptyItemStackHandler
        this.autoIoHandler = null
        this.ecImporter = null

        this.validInputModes = onlyNoneList
        this.validOutputModes = onlyNoneList
    }

    override fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (!this.hasSynchroParts) {
            val stack = player.getHeldItem(hand)
            val synchroParts = MetaItemClayParts.SynchronousParts.getStackForm()
            if (stack.isItemEqual(synchroParts) && stack.metadata == synchroParts.metadata) {
                this.hasSynchroParts = true
                if (!player.isCreative) stack.shrink(1)
                return
            }
        }
        val mimicTarget = this.target
        if (mimicTarget?.canOpenGui() == true) {
            val targetPos = mimicTarget.pos ?: return
            val targetWorld = mimicTarget.world ?: return
            MetaTileEntityGuiFactory.open(player, targetPos, targetWorld)
        }
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        throw UnsupportedOperationException("no direct gui for clay interfaces")
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.CLAY_ENERGY_HOLDER && target != null) {
            return target?.getCapability(capability, facing)
        }
        return super.getCapability(capability, facing)
    }
}