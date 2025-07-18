package com.github.trc.clayium.common.blocks.metalchest

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.screen.UISettings
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ButtonWidget
import com.cleanroommc.modularui.widgets.PagedWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.slot.ItemSlot
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.metatileentity.SyncedTileEntityBase
import com.github.trc.clayium.api.metatileentity.interfaces.IMarkDirty
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.asWidgetResizing
import com.github.trc.clayium.integration.modularui.MuiSlots
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
import net.minecraftforge.items.IItemHandlerModifiable
import kotlin.math.max
import kotlin.math.min

class TileEntityMetalChest : SyncedTileEntityBase(), ITickable, IGuiHolder<PosGuiData>, IMarkDirty {

    var inventoryHeight: Int = 0
    var inventoryWidth: Int = 0
    var inventoryPage: Int = 0
    var material: CMaterial = CMaterials.aluminum

    private lateinit var itemInventory: IItemHandlerModifiable
    private var customName: String? = null

    var prevLidAngle = 0f
        private set
    var lidAngle = 0f
        private set
    var facing: EnumFacing = EnumFacing.NORTH
        private set
    private var numPlayersUsing = 0

    fun init(row: Int, column: Int, page: Int, material: CMaterial) {
        this.inventoryWidth = row
        this.inventoryHeight = column
        this.inventoryPage = page
        this.material = material
        this.itemInventory = ClayiumItemStackHandler(this, row * column * page)
    }

    fun onBlockPlacedBy(placer: EntityLivingBase, stack: ItemStack) {
        this.facing = placer.horizontalFacing.opposite
        if (stack.hasDisplayName()) {
            this.customName = stack.displayName
        }
        if (!this.world.isRemote) this.markDirty()
    }

    override fun update() {
        this.prevLidAngle = this.lidAngle
        val open = this.numPlayersUsing > 0 && this.lidAngle == 0.0f
        if (open) {
            this.world.playSound(null, this.pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS,
                0.5f, this.world.rand.nextFloat() * 0.1f + 0.9f)
        }
        val opening = this.numPlayersUsing > 0 && this.lidAngle < 1.0f
        if (opening) {
            this.lidAngle = min(this.lidAngle + 0.1f, 1.0f)
        }
        val closing = this.numPlayersUsing == 0 && this.lidAngle > 0.0f
        if (closing) {
            this.lidAngle = max(this.lidAngle - 0.1f, 0.0f)
        }
        if (this.lidAngle < 0.5f && this.prevLidAngle >= 0.5f) {
            this.world.playSound(null, this.pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS,
                0.5f, this.world.rand.nextFloat() * 0.1f + 0.9f)
        }
    }

    // same as the Vanilla TileEntityChest
    override fun receiveClientEvent(id: Int, type: Int): Boolean {
        CLog.info("ClientEvent $id $type")
        if (id == 1) {
            this.numPlayersUsing = type
            return true
        }
        return super.receiveClientEvent(id, type)
    }

    fun onInventoryOpen(player: EntityPlayer) {
        CLog.info("InventoryOpen $numPlayersUsing")
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) this.numPlayersUsing = 0
            ++this.numPlayersUsing
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing)
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false)
        }
    }

    fun onInventoryClose(player: EntityPlayer) {
        CLog.info("InventoryClose $numPlayersUsing")
        if (!player.isSpectator()) {
            --this.numPlayersUsing
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing)
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false)
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        buf.writeResourceLocation(material.materialId)
        buf.writeVarInt(this.facing.index)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        this.material = ClayiumApi.materialRegistry.getObject(buf.readResourceLocation())
            ?: CMaterials.aluminum
        this.facing = EnumFacing.byIndex(buf.readVarInt())
        this.scheduleRenderUpdate()
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {}

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setInteger("inventoryRowSize", this.inventoryHeight)
        compound.setInteger("inventoryColumnSize", this.inventoryWidth)
        compound.setInteger("inventoryPage", this.inventoryPage)
        compound.setString("material", this.material.materialId.toString())
        compound.setInteger("facing", this.facing.index)
        CUtils.writeItems(itemInventory, "itemInventory", compound)
        if (this.customName != null) {
            compound.setString("CustomName", this.customName!!)
        }
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.inventoryHeight = compound.getInteger("inventoryRowSize")
        this.inventoryWidth = compound.getInteger("inventoryColumnSize")
        this.inventoryPage = compound.getInteger("inventoryPage")
        this.itemInventory = ClayiumItemStackHandler(this, this.inventoryHeight * this.inventoryWidth * this.inventoryPage)
        this.material = ClayiumApi.materialRegistry.getObject(ResourceLocation(compound.getString("material")))
            ?: CMaterials.aluminum
        CUtils.readItems(itemInventory, "itemInventory", compound)
        this.facing = EnumFacing.byIndex(compound.getInteger("facing"))
        if (compound.hasKey("CustomName", Constants.NBT.TAG_STRING)) {
            this.customName = compound.getString("CustomName")
        }
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ITEM_HANDLER_CAPABILITY) {
            return capability.cast(itemInventory)
        }
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === ITEM_HANDLER_CAPABILITY) {
            return true
        }
        return super.hasCapability(capability, facing)
    }

    override fun buildUI(data: PosGuiData, syncManager: PanelSyncManager, settings: UISettings): ModularPanel {
        syncManager.registerSlotGroup("metal_chest_inv", inventoryHeight)

        val columnStr = "I".repeat(inventoryWidth)
        val matrixStr = (0..<inventoryHeight).map { columnStr }

        val pageController = PagedWidget.Controller()
        val pagedWidget = PagedWidget()
            .controller(pageController)
        for (pageIndex in 0..<inventoryPage) {
            pagedWidget.addPage(
                SlotGroupWidget.builder()
                    .matrix(*matrixStr.toTypedArray())
                    .key('I') { slotIndex ->
                        ItemSlot().slot(SyncHandlers.itemSlot(itemInventory, slotIndex + (pageIndex * (inventoryHeight * inventoryWidth))))
                    }.build()
            )
        }

        val width = max(max(inventoryWidth, 9) * 18 + 14, GUI_DEFAULT_WIDTH + 56)
        val chestInventoryWidth = inventoryWidth * 18
        val playerInventoryWidth = 162
        val titleTextWidget = if (this.customName != null) {
            IKey.str(this.customName!!)
        } else {
            IKey.lang("tile.clayium.metal_chest", IKey.lang(material.translationKey))
        }
        syncManager.addOpenListener { this.onInventoryOpen(it) }
        syncManager.addCloseListener { this.onInventoryClose(it) }
        return ModularPanel.defaultPanel("metal_chest_inv", width, 18 + inventoryHeight * 18 + 94 + 2)
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(titleTextWidget.asWidget()
                        .top(0).left(((width - 7 * 2) - chestInventoryWidth) / 2))
                    .child(pagedWidget.alignX(Alignment.Center)
                        .margin(0, 9).height(18 * inventoryHeight).width(inventoryWidth * 18))
                    .child(IKey.lang("container.inventory").asWidget()
                        .bottom(0).left(((width - 7 * 2) - playerInventoryWidth) / 2)))
                .child(ParentWidget().right(0).bottom(14).width(12 + 2 + 12).height(12 + 4 + 9)
                    .child(ButtonWidget()
                        .onMousePressed {
                            pagedWidget.previousPage()
                            true
                        }
                        .overlay(IKey.str("<").shadow(false))
                        .align(Alignment.TopLeft)
                        .size(12, 12))
                    .child(ButtonWidget()
                        .onMousePressed {
                            pagedWidget.nextPage()
                            true
                        }
                        .overlay(IKey.str(">").shadow(false))
                        .align(Alignment.TopRight)
                        .size(12, 12))
                    .child(IKey.dynamic { "${pagedWidget.currentPageIndex + 1} / $inventoryPage" }.asWidgetResizing()
                        .align(Alignment.BottomCenter))
                )
                .child(MuiSlots.playerInventory(0)))
    }
}