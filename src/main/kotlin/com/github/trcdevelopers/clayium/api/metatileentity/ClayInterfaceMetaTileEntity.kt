package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.IMultiblockPart
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
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
import kotlin.contracts.contract

class ClayInterfaceMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: Int,
) : MetaTileEntity(metaTileEntityId, tier, listOf(MachineIoMode.NONE), listOf(MachineIoMode.NONE), "machine.${CValues.MOD_ID}.interface"), IMultiblockPart {

    override val faceTexture = clayiumId("blocks/interface")

    override var importItems: IItemHandlerModifiable = ItemStackHandler(0)
    override var exportItems: IItemHandlerModifiable = ItemStackHandler(0)
    override var itemInventory: IItemHandler = ItemStackHandler(0)
    override var autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    override var validInputModes: List<MachineIoMode> = listOf(MachineIoMode.NONE)
    override var validOutputModes: List<MachineIoMode> = listOf(MachineIoMode.NONE)

    var mimicTarget: MetaTileEntity? = null
        private set

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayInterfaceMetaTileEntity(metaTileEntityId, tier)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("interface"), "tier=$tier"))
    }

    override fun isAttachedToMultiblock(): Boolean {
        return this.mimicTarget != null
    }

    override fun addToMultiblock(controller: MultiblockControllerBase) {
        this.mimic(controller)
    }

    override fun removeFromMultiblock(controller: MultiblockControllerBase) {
        this.reInitialize()
    }

    override fun canPartShare() = false

    private fun mimic(target: MetaTileEntity) {
        this.mimicTarget = target
        this.importItems = target.importItems
        this.exportItems = target.exportItems
        this.itemInventory = ItemHandlerProxy(this.importItems, this.exportItems)
        this.autoIoHandler = AutoIoHandler.Combined(this)

        this.validInputModes = target.validInputModes
        this.validOutputModes = target.validOutputModes
    }

    private fun reInitialize() {
        this.mimicTarget = null
        this.importItems = ItemStackHandler(0)
        this.exportItems = ItemStackHandler(0)
        this.itemInventory = ItemStackHandler(0)
        this.autoIoHandler = AutoIoHandler.Combined(this)

        this.validOutputModes = emptyList()
        this.validOutputModes = emptyList()
    }

    fun isSynchronized(): Boolean {
        return false
    }

    override fun canOpenGui(): Boolean {
        return this.mimicTarget != null
    }

    override fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val mimicTarget = this.mimicTarget ?: return false
        return mimicTarget.onRightClick(player, hand, clickedSide, hitX, hitY, hitZ)
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        throw UnsupportedOperationException("no direct gui for clay interfaces")
    }
}