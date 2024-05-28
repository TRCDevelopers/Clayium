package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.INTERFACE_SYNC_MIMIC_TARGET
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.ISynchronizedInterface
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.IMultiblockPart
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.MultiblockControllerBase
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.ProxyMetaTileEntityBase
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler

class ClayInterfaceMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, onlyNoneList, onlyNoneList, "machine.${CValues.MOD_ID}.interface"), IMultiblockPart, ISynchronizedInterface {

    override val faceTexture = clayiumId("blocks/interface")
    override val useFaceForAllSides = true

    override var importItems: IItemHandlerModifiable = ItemStackHandler(0)
    override var exportItems: IItemHandlerModifiable = ItemStackHandler(0)
    override var itemInventory: IItemHandler = ItemStackHandler(0)
    override var autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)
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
        this.importItems = target.importItems
        this.exportItems = target.exportItems
        this.itemInventory = ItemHandlerProxy(this.importItems, this.exportItems)
        this.autoIoHandler = AutoIoHandler.Combined(this)
        target.getCapability(ClayiumTileCapabilities.CAPABILITY_CLAY_ENERGY_HOLDER, null)?.let { targetEnergyHolder ->
            this.ecImporter = AutoIoHandler.EcImporter(this, targetEnergyHolder.energizedClayItemHandler)
        }

        this.validInputModes = target.validInputModes
        this.validOutputModes = target.validOutputModes
        writeTargetData(target)
    }

    override fun onUnlink() {
        this.importItems = ItemStackHandler(0)
        this.exportItems = ItemStackHandler(0)
        this.itemInventory = ItemStackHandler(0)
        this.autoIoHandler = AutoIoHandler.Combined(this)
        this.ecImporter = null

        this.validInputModes = onlyNoneList
        this.validOutputModes = onlyNoneList
        writeTargetRemoved()
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