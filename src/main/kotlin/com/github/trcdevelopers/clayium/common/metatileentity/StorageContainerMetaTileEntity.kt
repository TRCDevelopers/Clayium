package com.github.trcdevelopers.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.NumberFormat
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_ITEMS_STORED
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_MAX_ITEMS_STORED
import com.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trcdevelopers.clayium.api.metatileentity.AutoIoHandler
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.client.model.ModelTextures
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.util.transferTo
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
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
            return filterSlot.getStackInSlot(0).canActuallyStack(stack)
                    && currentInsertedStack.canActuallyStack(stack)
                    && exportItems.getStackInSlot(0).canActuallyStack(stack)
        }
    }
    override val exportItems: IItemHandlerModifiable = ClayiumItemStackHandler(this, 1)
    override val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    // phantom slot. should I support "IItemFilter"? not supported in the original version.
    private val filterSlot = ItemStackHandler(1)
    private var currentInsertedStack: ItemStack = ItemStack.EMPTY

    private var maxStoredItems = INITIAL_MAX_AMOUNT
        set(value) {
            val syncFlag = !isRemote && field != value
            field = value
            markDirty()
            if (syncFlag) writeCustomData(UPDATE_MAX_ITEMS_STORED) { writeVarInt(value) }
        }

    private var itemsStored = 0
        set(value) { field = value; markDirty() }

    private var previousStoredItems = 0
    override fun update() {
        super.update()
        if (world?.isRemote == true) return

        if (itemsStored < maxStoredItems) {
            val inputStack = importItems.getStackInSlot(0)
            if (!inputStack.isEmpty) {
                importItems.transferTo(itemInventory)
            }
            if (offsetTimer % 10L == 0L && previousStoredItems != itemsStored) {
                writeCustomData(UPDATE_ITEMS_STORED) { writeVarInt(itemsStored) }
                previousStoredItems = itemsStored
            }
        }

        if (itemsStored > 0) {
            itemInventory.transferTo(exportItems)
        }
    }

    override fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        val stack = player.getHeldItem(hand)
        val clayCore = MetaItemClayParts.CLAY_CORE.getStackForm()
        //todo use capability
        if (stack.isItemEqual(clayCore) && stack.metadata == clayCore.metadata && maxStoredItems == INITIAL_MAX_AMOUNT) {
            maxStoredItems = UPGRADED_MAX_AMOUNT
            stack.shrink(1)
            return
        }
        super.onRightClick(player, hand, clickedSide, hitX, hitY, hitZ)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return StorageContainerMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_ITEMS_STORED -> itemsStored = buf.readVarInt()
            UPDATE_MAX_ITEMS_STORED -> {
                maxStoredItems = buf.readVarInt()
                scheduleRenderUpdate()
            }
            else -> super.receiveCustomData(discriminator, buf)
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        buf.writeVarInt(maxStoredItems)
        buf.writeVarInt(itemsStored)
        if (currentInsertedStack.isEmpty) {
            buf.writeBoolean(true)
            buf.writeItemStack(exportItems.getStackInSlot(0))
        } else {
            buf.writeBoolean(false)
            buf.writeItemStack(currentInsertedStack)
        }
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        super.receiveInitialSyncData(buf)
        maxStoredItems = buf.readVarInt()
        itemsStored = buf.readVarInt()
        if (buf.readBoolean()) {
            currentInsertedStack = ItemStack.EMPTY
            exportItems.setStackInSlot(0, buf.readItemStack())
        } else {
            currentInsertedStack = buf.readItemStack()
        }
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

    override fun writeItemStackNbt(data: NBTTagCompound) {
        data.setBoolean("upgraded", maxStoredItems == UPGRADED_MAX_AMOUNT)
        data.setInteger("itemsStored", itemsStored)
        data.setTag("storedStack", currentInsertedStack.serializeNBT())
        data.setTag("outputStack", exportItems.getStackInSlot(0).serializeNBT())
    }

    override fun readItemStackNbt(data: NBTTagCompound) {
        this.maxStoredItems = if (data.getBoolean("upgraded")) UPGRADED_MAX_AMOUNT else INITIAL_MAX_AMOUNT
        this.itemsStored = data.getInteger("itemsStored")
        this.currentInsertedStack = ItemStack(data.getCompoundTag("storedStack"))
        this.exportItems.setStackInSlot(0, ItemStack(data.getCompoundTag("outputStack")))
    }

    override fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {}

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("storage_container"), "inventory"))
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.syncValue("itemsStored", SyncHandlers.intNumber(::itemsStored, ::itemsStored::set))
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
                        .child(largeSlot(SyncHandlers.itemSlot(importItems, 0).singletonSlotGroup())
                            .align(Alignment.CenterLeft))
                        .child(largeSlot(SyncHandlers.itemSlot(exportItems, 0).accessibility(/* canPut = */ false, /* canTake = */ true))
                            .align(Alignment.CenterRight))
                    .align(Alignment.Center))
                    .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(filterSlot, 0))
                        .right(10).top(15))
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

    override fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {
        val stack = if (currentInsertedStack.isEmpty) exportItems.getStackInSlot(0) else currentInsertedStack
        if (stack.isEmpty) return

        val mc = Minecraft.getMinecraft()
        GlStateManager.pushMatrix()
        run {
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5)

            when (this.frontFacing) {
                EnumFacing.NORTH -> GlStateManager.rotate(180f, 0f, 1f, 0f)
                EnumFacing.WEST -> GlStateManager.rotate(270f, 0f, 1f, 0f)
                EnumFacing.EAST -> GlStateManager.rotate(90f, 0f, 1f, 0f)
                else -> {}
            }

            GlStateManager.pushMatrix()
            GlStateManager.translate(0.0, 0.125, 0.51)
            GlStateManager.scale(0.5f, 0.5f, 0.5f)
            mc.renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED)
            GlStateManager.popMatrix()

            GlStateManager.pushMatrix()
            val amountText: String = NumberFormat.formatWithMaxDecimals(itemsStored.toDouble(), 4)
            val fRenderer = mc.fontRenderer
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
            GlStateManager.translate(0.0, -0.15, -0.55)
            GlStateManager.scale(-0.025f, -0.025f, 0.025f)
            fRenderer.drawString(amountText, -fRenderer.getStringWidth(amountText) / 2, 0, 0)
            GlStateManager.popMatrix()
        }
        GlStateManager.popMatrix()

    }

    private fun ItemStack.canActuallyStack(stack: ItemStack): Boolean {
        return this.isEmpty || stack.isEmpty || ItemHandlerHelper.canItemStacksStack(this, stack)
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
            if (!(filterSlot.getStackInSlot(0).canActuallyStack(stack)
                        && currentInsertedStack.canActuallyStack(stack)
                        && exportItems.getStackInSlot(0).canActuallyStack(stack))) return stack

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