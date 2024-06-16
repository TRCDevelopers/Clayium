package com.github.trcdevelopers.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widget.Widget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_ITEMS_STORED
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_MAX_ITEMS_STORED
import com.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trcdevelopers.clayium.api.metatileentity.AutoIoHandler
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.client.model.ModelTextures
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler

class StorageContainerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, validOutputModesLists[1], "machine.${CValues.MOD_ID}.storage_container") {

    override val faceTexture = clayiumId("blocks/storage_container")
    override val requiredTextures = listOf(faceTexture, clayiumId("blocks/storage_container_side"), clayiumId("blocks/storage_container_top"))

    override val itemInventory: IItemHandler = StorageContainerItemHandler()
    override val importItems: IItemHandlerModifiable = object : ClayiumItemStackHandler(this, 1) {
        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            if (!isItemValid(slot, stack)) return stack
            return itemInventory.insertItem(slot, stack, simulate)
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return checkFilterSlot(stack) && checkCurrentStack(stack)
        }
    }
    override val exportItems: IItemHandlerModifiable = ClayiumItemStackHandler(this, 1)
    override val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    // phantom slot. should I support "IItemFilter"? not supported in the original version.
    private val filterSlot = ItemStackHandler(1)
    private var currentInsertedStack = ItemStack.EMPTY

    private var maxStoredItems = INITIAL_MAX_AMOUNT
        set(value) { field = value; markDirty() }

    private var itemsStored = 0
        set(value) { field = value; markDirty() }

    private var previousStoredItems = 0
    override fun update() {
        super.update()
        if (world?.isRemote == true) return

        if (itemsStored < maxStoredItems) {
            val inputStack = importItems.getStackInSlot(0)
            if (!inputStack.isEmpty) {
                TransferUtils.moveItems(importItems, itemInventory)
            }
            Clayium.LOGGER.info(itemsStored)
            if (previousStoredItems != itemsStored) {
                writeCustomData(UPDATE_ITEMS_STORED) { writeVarInt(itemsStored) }
                previousStoredItems = itemsStored
            }
        }

        if (itemsStored > 0) {
            TransferUtils.moveItems(itemInventory, exportItems)
        }
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return StorageContainerMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_ITEMS_STORED -> itemsStored = buf.readVarInt()
            UPDATE_MAX_ITEMS_STORED -> maxStoredItems = buf.readVarInt()
            else -> super.receiveCustomData(discriminator, buf)
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        writeCustomData(UPDATE_ITEMS_STORED) { writeVarInt(itemsStored) }
        writeCustomData(UPDATE_MAX_ITEMS_STORED) { writeVarInt(maxStoredItems) }
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setInteger("maxStoredItems", maxStoredItems)
        data.setInteger("itemsStored", itemsStored)
        data.setTag("storedStack", currentInsertedStack.serializeNBT())
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        if (data.hasKey("maxStoredItems", Constants.NBT.TAG_INT)) { maxStoredItems = data.getInteger("maxStoredItems") }
        if (data.hasKey("itemsStored", Constants.NBT.TAG_INT)) { itemsStored = data.getInteger("itemsStored") }
        if (data.hasKey("storedStack", Constants.NBT.TAG_COMPOUND)) { currentInsertedStack = ItemStack(data.getCompoundTag("storedStack")) }
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("storage_container"), "inventory"))
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("storage_container")
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(translationKey).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(IKey.dynamic { "$itemsStored / $maxStoredItems" }.asWidget()
                        .widthRel(0.5f).align(Alignment.BottomRight))
                    .child(Column().widthRel(0.6f).height(26)
                        .child(ItemSlot().slot(SyncHandlers.itemSlot(importItems, 0).singletonSlotGroup())
                            .align(Alignment.CenterLeft))
                        .child(ItemSlot().slot(SyncHandlers.itemSlot(exportItems, 0).accessibility(/* canPut = */ false, /* canTake = */ true))
                            .align(Alignment.CenterRight))
                    .align(Alignment.Center))
                )
                .child(SlotGroupWidget.playerInventory(0)))
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(bakedTexGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        sideQuads = EnumFacing.HORIZONTALS.associateWith {
            ModelTextures.createQuad(it, bakedTexGetter.apply(clayiumId("blocks/storage_container_side")))
        }
        topQuad = ModelTextures.createQuad(EnumFacing.UP, bakedTexGetter.apply(clayiumId("blocks/storage_container_top")))
    }

    @SideOnly(Side.CLIENT)
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        if (state == null || side == null) return super.getQuads(state, side, rand)
        val quads = super.getQuads(state, side, rand)
        when {
            side.axis.isHorizontal -> sideQuads[side]?.let { quads.add(it) }
            side == EnumFacing.UP -> quads.add(topQuad)
        }
        return quads
    }

    private fun checkFilterSlot(stack: ItemStack): Boolean {
        val filterStack = filterSlot.getStackInSlot(0)
        return filterStack.isEmpty || ItemHandlerHelper.canItemStacksStack(stack, filterStack)
    }

    private fun checkCurrentStack(stack: ItemStack): Boolean {
        return currentInsertedStack.isEmpty || ItemHandlerHelper.canItemStacksStack(stack, currentInsertedStack)
    }

    private inner class StorageContainerItemHandler : IItemHandler {
        override fun getSlots() = 1
        override fun getSlotLimit(slot: Int) = maxStoredItems

        override fun getStackInSlot(slot: Int): ItemStack {
            val stack = currentInsertedStack
            if (stack.isEmpty || itemsStored == 0) return ItemStack.EMPTY

            return stack.copy().apply { count = itemsStored }
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            if (stack.isEmpty) return ItemStack.EMPTY
            if (!(checkFilterSlot(stack) && checkCurrentStack(stack))) return stack

            val amountToInsert = stack.count.coerceAtMost(maxStoredItems - itemsStored)
            val copiedStack = stack.copy()
            copiedStack.shrink(amountToInsert)

            if (!simulate && amountToInsert > 0) {
                if (currentInsertedStack.isEmpty) {
                    currentInsertedStack = stack.copy().apply { count = 1 }
                    itemsStored = amountToInsert
                } else {
                    itemsStored += amountToInsert
                }
            }
            return copiedStack
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (currentInsertedStack.isEmpty || itemsStored == 0) return ItemStack.EMPTY
            val amountToExtract = amount.coerceAtMost(itemsStored)
            val extractedStack = currentInsertedStack.copy().apply { count = amountToExtract }
            if (!simulate) {
                itemsStored -= amountToExtract
                if (itemsStored == 0) currentInsertedStack = ItemStack.EMPTY
            }
            return extractedStack
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return super.isItemValid(slot, stack)
        }
    }

    companion object {
        const val INITIAL_MAX_AMOUNT = 65536
        const val UPGRADED_MAX_AMOUNT = Int.MAX_VALUE

        @SideOnly(Side.CLIENT)
        private lateinit var sideQuads: Map<EnumFacing, BakedQuad>

        @SideOnly(Side.CLIENT)
        private lateinit var topQuad: BakedQuad
    }
}