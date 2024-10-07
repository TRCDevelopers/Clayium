package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.gui.MetaTileEntityGuiFactory
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.multiblock.ProxyMetaTileEntityBase
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import java.lang.ref.WeakReference

class ClayInterfaceMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, "clay_interface") {

    override val faceTexture = clayiumId("blocks/clay_interface")
    override val useFaceForAllSides = true

    override val importItems: IItemHandlerModifiable get() = targetImportItems.get() ?: EmptyItemStackHandler
    override val exportItems: IItemHandlerModifiable get() = targetExportItems.get() ?: EmptyItemStackHandler
    override val itemInventory: IItemHandler get() = targetItemInventory.get() ?: EmptyItemStackHandler

    private var targetImportItems: WeakReference<IItemHandlerModifiable> = WeakReference(null)
    private var targetExportItems: WeakReference<IItemHandlerModifiable> = WeakReference(null)
    private var targetItemInventory: WeakReference<IItemHandler> = WeakReference(null)

    private var ecImporter: AutoIoHandler.EcImporter? = null
    private var autoIoHandler: AutoIoHandler? = null

    override var validInputModes: List<MachineIoMode> = onlyNoneList
    override var validOutputModes: List<MachineIoMode> = onlyNoneList

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayInterfaceMetaTileEntity(metaTileEntityId, tier)
    }

    override fun linkTo(target: MetaTileEntity) {
        super.linkTo(target)
        this.targetImportItems = WeakReference(target.importItems)
        this.targetExportItems = WeakReference(target.exportItems)
        this.targetItemInventory = WeakReference(target.itemInventory)
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

    override fun unlink() {
        super.unlink()
        this.targetImportItems = WeakReference(null)
        this.targetExportItems = WeakReference(null)
        this.targetItemInventory = WeakReference(null)
        this.autoIoHandler = null
        this.ecImporter = null

        this.validInputModes = onlyNoneList
        this.validOutputModes = onlyNoneList
    }

    override fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (super.onRightClick(player, hand, clickedSide, hitX, hitY, hitZ)) {
            return true
        }
        val mimicTarget = this.target
        if (mimicTarget?.canOpenGui() == true) {
            val targetPos = mimicTarget.pos ?: return false
            val targetWorld = mimicTarget.world ?: return false
            MetaTileEntityGuiFactory.open(player, targetPos, targetWorld)
            return true
        } else {
            return false
        }
    }

    override fun canOpenGui() = false
    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        throw UnsupportedOperationException("no direct gui for clay interfaces")
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.CLAY_ENERGY_HOLDER && target != null) {
            return target!!.getCapability(capability, facing)
        }
        return super.getCapability(capability, facing)
    }
}