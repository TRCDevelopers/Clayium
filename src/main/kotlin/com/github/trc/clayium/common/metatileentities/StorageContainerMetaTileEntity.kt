package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.NumberFormat
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.block.BlockMachine
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_FILTER_ITEM
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_ITEMS_STORED
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_MAX_ITEMS_STORED
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_STORED_ITEMSTACK
import com.github.trc.clayium.api.capability.IPipeConnectionLogic
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.copyWithSize
import com.github.trc.clayium.client.model.ModelTextures
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.util.transferTo
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
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
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import kotlin.math.min

class StorageContainerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    isUpgraded: Boolean,
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, validOutputModesLists[1], "storage_container") {

    override val faceTexture = clayiumId("blocks/storage_container")
    override val requiredTextures get() = listOf(
        faceTexture,
        clayiumId("blocks/storage_container_side_composed"), clayiumId("blocks/storage_container_side_upgraded"),
        clayiumId("blocks/storage_container_top_composed"), clayiumId("blocks/storage_container_top_upgraded"),
        clayiumId("blocks/storage_container_upgraded_base")
    )

    override val pipeConnectionLogic: IPipeConnectionLogic = IPipeConnectionLogic.ItemPipe

    override val itemInventory: IItemHandler = StorageContainerItemHandler()
    override val exportItems: IItemHandlerModifiable = StorageContainerExportItems()
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
    val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    private val filterSlot = ItemStackHandler(1)
    private var currentInsertedStack: ItemStack = ItemStack.EMPTY

    private var maxStoredItems = if (isUpgraded) UPGRADED_MAX_AMOUNT else INITIAL_MAX_AMOUNT
        set(value) {
            val syncFlag = !isRemote && field != value
            field = value
            markDirty()
            if (syncFlag) writeCustomData(UPDATE_MAX_ITEMS_STORED) { writeVarInt(value) }
        }
    val isUpgraded get() = maxStoredItems == UPGRADED_MAX_AMOUNT

    private var itemsStored = 0
        set(value) { field = value; markDirty() }

    private var previousStoredItems = 0
    override fun update() {
        super.update()
        if (isRemote) return

        if (itemsStored < maxStoredItems) {
            val inputStack = importItems.getStackInSlot(0)
            if (!inputStack.isEmpty) {
                importItems.transferTo(itemInventory)
            }
        }

        if (previousStoredItems != itemsStored) {
            writeCustomData(UPDATE_ITEMS_STORED) { writeVarInt(itemsStored) }
            previousStoredItems = itemsStored
        }
    }

    override fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        val stack = player.getHeldItem(hand)
        val clayCore = MetaItemClayParts.ClayCore.getStackForm()
        //todo use capability
        if (stack.isItemEqual(clayCore) && stack.metadata == clayCore.metadata && maxStoredItems == INITIAL_MAX_AMOUNT) {
            val world = this.world
            val pos = this.pos
            if (!(world == null || pos == null)) {
                val upgradedStorageContainerStack = MetaTileEntities.STORAGE_CONTAINER_UPGRADED.getStackForm()
                upgradedStorageContainerStack.tagCompound = NBTTagCompound().apply { writeItemStackNbt(this) }
                this.blockMachine.onBlockPlacedBy(world, pos, world.getBlockState(pos), player, upgradedStorageContainerStack)
                stack.shrink(1)
                return
            }
        }
        super.onRightClick(player, hand, clickedSide, hitX, hitY, hitZ)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return capability.cast(createFilteredItemHandler(itemInventory, facing))
        }
        return super.getCapability(capability, facing)
    }

    override fun canBeReplacedTo(world: World, pos: BlockPos, sampleMetaTileEntity: MetaTileEntity): Boolean {
        if (sampleMetaTileEntity !is StorageContainerMetaTileEntity) return false
        if (this.isUpgraded && !sampleMetaTileEntity.isUpgraded) return false
        return super.canBeReplacedTo(world, pos, sampleMetaTileEntity)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return StorageContainerMetaTileEntity(this.metaTileEntityId, this.tier, maxStoredItems == UPGRADED_MAX_AMOUNT)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_ITEMS_STORED -> itemsStored = buf.readVarInt()
            UPDATE_MAX_ITEMS_STORED -> {
                maxStoredItems = buf.readVarInt()
                scheduleRenderUpdate()
            }
            UPDATE_STORED_ITEMSTACK -> currentInsertedStack = buf.readItemStack()
            UPDATE_FILTER_ITEM -> filterSlot.deserializeNBT(buf.readCompoundTag())
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
        buf.writeCompoundTag(filterSlot.serializeNBT())
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
        filterSlot.deserializeNBT(buf.readCompoundTag())
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setInteger("maxStoredItems", maxStoredItems)
        data.setInteger("itemsStored", itemsStored)
        data.setTag("storedStack", currentInsertedStack.serializeNBT())
        data.setTag("filterSlot", filterSlot.serializeNBT())
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        if (data.hasKey("maxStoredItems", Constants.NBT.TAG_INT)) { maxStoredItems = data.getInteger("maxStoredItems") }
        if (data.hasKey("itemsStored", Constants.NBT.TAG_INT)) { itemsStored = data.getInteger("itemsStored") }
        if (data.hasKey("storedStack", Constants.NBT.TAG_COMPOUND)) { currentInsertedStack = ItemStack(data.getCompoundTag("storedStack")) }
        if (data.hasKey("filterSlot", Constants.NBT.TAG_COMPOUND)) { filterSlot.deserializeNBT(data.getCompoundTag("filterSlot")) }
    }

    override fun writeItemStackNbt(data: NBTTagCompound) {
        data.setInteger("itemsStored", itemsStored)
        data.setTag("storedStack", currentInsertedStack.serializeNBT())
        data.setTag("outputStack", exportItems.getStackInSlot(0).serializeNBT())
        data.setTag("filterSlot", filterSlot.serializeNBT())
    }

    override fun readItemStackNbt(data: NBTTagCompound) {
        this.itemsStored = data.getInteger("itemsStored")
        this.currentInsertedStack = ItemStack(data.getCompoundTag("storedStack"))
        this.exportItems.setStackInSlot(0, ItemStack(data.getCompoundTag("outputStack")))
        this.filterSlot.deserializeNBT(data.getCompoundTag("filterSlot"))

        previousStoredItems = itemsStored
        writeCustomData(UPDATE_ITEMS_STORED) { writeVarInt(itemsStored) }
        writeCustomData(UPDATE_STORED_ITEMSTACK) { writeItemStack(currentInsertedStack) }
        writeCustomData(UPDATE_FILTER_ITEM) { writeCompoundTag(filterSlot.serializeNBT()) }
    }

    override fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {}

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(this.metaTileEntityId, "inventory"))
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
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
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(getter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        sideQuads = EnumFacing.HORIZONTALS.associateWith {
            ModelTextures.createQuad(it, getter.apply(clayiumId("blocks/storage_container_side_composed")))
        }
        sideQuadsUpgraded = EnumFacing.entries.map {
            when (it) {
                EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST
                    -> ModelTextures.createQuad(it, getter.apply(clayiumId("blocks/storage_container_side_upgraded")))
                EnumFacing.UP
                    -> ModelTextures.createQuad(it, getter.apply(clayiumId("blocks/storage_container_top_upgraded")))
                EnumFacing.DOWN
                    -> ModelTextures.createQuad(it, getter.apply(clayiumId("blocks/storage_container_upgraded_base")))
            }
        }
        upQuad = ModelTextures.createQuad(EnumFacing.UP, getter.apply(clayiumId("blocks/storage_container_top_composed")))
        downQuad = ModelTextures.createQuad(EnumFacing.DOWN, getter.apply(clayiumId("blocks/storage_container_top_composed")),
            uv = floatArrayOf(16f, 16f, 0f, 0f))
    }

    @SideOnly(Side.CLIENT)
    override fun getQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        if (state == null || side == null) return
        val isUpgraded = this.maxStoredItems == UPGRADED_MAX_AMOUNT
        if (isUpgraded) {
            quads.add(sideQuadsUpgraded[side.index])
        } else {
            when (side) {
                EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST
                    -> sideQuads[side]?.let(quads::add)
                EnumFacing.UP -> quads.add(upQuad)
                EnumFacing.DOWN -> quads.add(downQuad)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {
        val pos = this.pos ?: return
        if (world?.getBlockState(pos)?.getValue(BlockMachine.IS_PIPE) == true) return

        val stack = if (currentInsertedStack.isEmpty) filterSlot.getStackInSlot(0) else currentInsertedStack
        if (stack.isEmpty) return

        val mc = Minecraft.getMinecraft()
        GlStateManager.pushMatrix()
        run {
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5)

            GlStateManager.pushMatrix()
            when (this.frontFacing) {
                EnumFacing.NORTH -> {}
                EnumFacing.WEST -> GlStateManager.rotate(90f, 0f, 1f, 0f)
                EnumFacing.EAST -> GlStateManager.rotate(270f, 0f, 1f, 0f)
                else -> GlStateManager.rotate(180f, 0f, 1f, 0f)
            }
            GlStateManager.translate(0.0, 0.125, -0.51)
            GlStateManager.scale(0.5f, 0.5f, 0.5f)
            RenderHelper.enableStandardItemLighting()
            mc.renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED)
            RenderHelper.disableStandardItemLighting()
            GlStateManager.popMatrix()

            when (this.frontFacing) {
                EnumFacing.NORTH -> GlStateManager.rotate(180f, 0f, 1f, 0f)
                EnumFacing.WEST -> GlStateManager.rotate(270f, 0f, 1f, 0f)
                EnumFacing.EAST -> GlStateManager.rotate(90f, 0f, 1f, 0f)
                else -> {}
            }

            GlStateManager.pushMatrix()
            val amountText: String = NumberFormat.formatWithMaxDigits(itemsStored.toDouble(), 3)
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
                    writeCustomData(UPDATE_STORED_ITEMSTACK) { writeItemStack(currentInsertedStack) }
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

    private inner class StorageContainerExportItems : IItemHandlerModifiable {
        override fun setStackInSlot(slot: Int, stack: ItemStack) {
            // this method should be called only for exporting item.
            if (slot != 0 || stack.isItemEqual(currentInsertedStack)) return
            val currentSlotAmount = min(itemsStored, 64)
            val amountAfterExtraction = stack.count
            val extractedAmount = currentSlotAmount - amountAfterExtraction
            if (extractedAmount > 0) {
                extractItem(0, extractedAmount, false)
            }
        }
        override fun getSlots() = 1
        override fun getSlotLimit(slot: Int) = 64

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            return itemInventory.insertItem(0, stack, simulate)
        }
        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            return itemInventory.extractItem(0, amount, simulate)
        }

        override fun getStackInSlot(slot: Int): ItemStack {
            val stackSize = min(itemsStored, 64)
            return currentInsertedStack.copyWithSize(stackSize)
        }
    }

    companion object {
        const val INITIAL_MAX_AMOUNT = 65536
        const val UPGRADED_MAX_AMOUNT = Int.MAX_VALUE

        @SideOnly(Side.CLIENT)
        private lateinit var sideQuads: Map<EnumFacing, BakedQuad>
        @SideOnly(Side.CLIENT)
        private lateinit var sideQuadsUpgraded: List<BakedQuad>
        @SideOnly(Side.CLIENT)
        private lateinit var upQuad: BakedQuad
        @SideOnly(Side.CLIENT)
        private lateinit var downQuad: BakedQuad
    }
}