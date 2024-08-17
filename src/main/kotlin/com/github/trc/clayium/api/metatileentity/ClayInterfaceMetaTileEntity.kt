package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.metatileentity.multiblock.ProxyMetaTileEntityBase
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class ClayInterfaceMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, "machine.${CValues.MOD_ID}.proxy") {

    override val faceTexture = clayiumId("blocks/interface")
    override val useFaceForAllSides = true

    override var importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override var exportItems: IItemHandlerModifiable = EmptyItemStackHandler
    override var itemInventory: IItemHandler = EmptyItemStackHandler
    var autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)
    private var ecImporter: AutoIoHandler.EcImporter? = null

    override var validInputModes: List<MachineIoMode> = onlyNoneList
    override var validOutputModes: List<MachineIoMode> = onlyNoneList

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayInterfaceMetaTileEntity(metaTileEntityId, tier)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("interface"), "tier=${tier.numeric}"))
    }

    override fun onLink(target: MetaTileEntity) {
        super.onLink(target)
        this.importItems = target.importItems
        this.exportItems = target.exportItems
        this.itemInventory = ItemHandlerProxy(this.importItems, this.exportItems)
        this.autoIoHandler = AutoIoHandler.Combined(this)
        target.getCapability(ClayiumTileCapabilities.CLAY_ENERGY_HOLDER, null)?.let { targetEnergyHolder ->
            this.ecImporter = AutoIoHandler.EcImporter(this, targetEnergyHolder.energizedClayItemHandler)
        }

        this.validInputModes = target.validInputModes
        this.validOutputModes = target.validOutputModes
    }

    override fun onUnlink() {
        super.onUnlink()
        this.importItems = EmptyItemStackHandler
        this.exportItems = EmptyItemStackHandler
        this.itemInventory = EmptyItemStackHandler
        this.autoIoHandler = AutoIoHandler.Combined(this)
        this.ecImporter = null

        this.validInputModes = onlyNoneList
        this.validOutputModes = onlyNoneList
    }

    fun isSynchronized(): Boolean {
        return false
    }

    override fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        val mimicTarget = this.target ?: return
        if (mimicTarget.canOpenGui()) {
            mimicTarget.onRightClick(player, hand, clickedSide, hitX, hitY, hitZ)
        }
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        throw UnsupportedOperationException("no direct gui for clay interfaces")
    }
}