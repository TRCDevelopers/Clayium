package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.GUI_DEFAULT_HEIGHT
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.capability.impl.FilteredItemHandler
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.capability.impl.RangedItemHandlerProxy
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode.ALL
import com.github.trc.clayium.api.util.canStackWith
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.blocks.ItemBlockMaterial
import com.github.trc.clayium.common.gui.ClayGuiTextures
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.min

private const val ROWS = 4
private const val COLS = 5
private const val EXPOSED_INV_SIZE = (ROWS - 1) * COLS

class AutoClayCondenserMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1], name = "auto_clay_condenser") {

    override val faceTexture = clayiumId("blocks/auto_clay_condenser")

    override val itemInventory = object : NotifiableItemStackHandler(this, ROWS * COLS, this, isExport = false) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return getMaterial(stack)?.getPropOrNull(CPropertyKey.CLAY) != null
        }
    }
    override val importItems = RangedItemHandlerProxy(itemInventory, 0..EXPOSED_INV_SIZE)
    override val exportItems = itemInventory
    @Suppress("Unused") private val ioHandler = AutoIoHandler.Combined(this)

    private val maxCompressedClay = object : ClayiumItemStackHandler(this, 1) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return getMaterial(stack)?.getPropOrNull(CPropertyKey.CLAY) != null
        }
    }

    override fun update() {
        super.update()
        if (isRemote) return
        if (!hasNotifiedInputs) return
        // compress clays
        if (this.tier.numeric >= 7) {
            compressClayMk2()
        } else {
            compressClayMk1()
        }
        sortInv()
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.registerSlotGroup("compressor_inventory", 4)
        val matrix = (0..<ROWS).map { "I".repeat(COLS) }.toTypedArray()
        return ModularPanel.defaultPanel("auto_clay_condenser", GUI_DEFAULT_WIDTH, GUI_DEFAULT_HEIGHT + 20)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager)
                    .child(Row().widthRel(1f).height(18 * 4)
                        .align(Alignment.Center)
                        .child(SlotGroupWidget.builder()
                            .matrix(*matrix)
                            .key('I') {
                                ItemSlot().slot(SyncHandlers.itemSlot(itemInventory, it)
                                    .filter { getMaterial(it)?.getPropOrNull(CPropertyKey.CLAY) != null }
                                    .slotGroup("compressor_inventory"))
                            }
                            .build().align(Alignment.Center))
                        .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(maxCompressedClay, 0)
                            .filter { getMaterial(it)?.getPropOrNull(CPropertyKey.CLAY) != null })
                            .align(Alignment.TopRight)
                            .background(ClayGuiTextures.CLAY_SLOT))
                    )
                )
            }
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return AutoClayCondenserMetaTileEntity(metaTileEntityId, tier)
    }

    private fun getMaterial(stack: ItemStack): CMaterial? {
        if (stack.isEmpty) return null
        val item = stack.item
        if (item !is ItemBlockMaterial) return null
        return item.blockMaterial.getCMaterial(stack)
    }

    private fun sortInv() {
        for (i in 0..<itemInventory.slots) {
            val sortingStack = itemInventory.getStackInSlot(i)
            if (sortingStack.isEmpty) continue
            val remainSorting = sortingStack.copy()
            for (j in 0..<i) {
                val targetStack = itemInventory.getStackInSlot(j)
                if (!targetStack.canStackWith(sortingStack)) continue
                val amountExtracted = min(targetStack.maxStackSize - targetStack.count, remainSorting.count)
                itemInventory.extractItem(i, amountExtracted, false)
                itemInventory.insertItem(j, remainSorting.splitStack(amountExtracted), false)
                if (remainSorting.isEmpty) break
            }
        }
        val stacks = mutableListOf<ItemStack>()
        for (i in 0..<itemInventory.slots) {
            val stack = itemInventory.extractItem(i, Int.MAX_VALUE, false)
            if (stack.isEmpty) break
            stacks.add(stack)
        }
        stacks.sortBy { getMaterial(it)?.tier?.numeric }
        for ((i, s) in stacks.withIndex()) {
            itemInventory.insertItem(i, s, false)
        }
    }

    private fun compressClayMk1() {
        // no space
        if (!itemInventory.getStackInSlot(ROWS * COLS - 1).isEmpty) return
        val maxCompress = getMaterial(maxCompressedClay.getStackInSlot(0))?.tier?.numeric ?: Int.MAX_VALUE
        for (i in (itemInventory.slots - 2) downTo 0) {
            val stack = itemInventory.getStackInSlot(i)
            if (stack.count < 9) continue
            val m = getMaterial(stack)
            val clay = m?.getPropOrNull(CPropertyKey.CLAY)
            val compressed = clay?.compressedInto
            if (m == null || m.tier == null || m.tier.numeric >= maxCompress || clay == null || compressed == null)
                continue
            val compressedStack = OreDictUnifier.get(OrePrefix.block, compressed, stackSize = 1)
            itemInventory.extractItem(i, 9, false)
            ItemHandlerHelper.insertItem(itemInventory, compressedStack, false)
            break
        }
    }

    private fun compressClayMk2() {
        val maxCompress = getMaterial(maxCompressedClay.getStackInSlot(0))?.tier?.numeric ?: Int.MAX_VALUE
        val storedItems = (0..<itemInventory.slots).map { itemInventory.extractItem(it, Int.MAX_VALUE, false) }
        for (stack in storedItems) {
            if (stack.isEmpty) continue
            val m = getMaterial(stack)
            val clay = m?.getPropOrNull(CPropertyKey.CLAY)
            val compressed = clay?.compressedInto
            if (m == null || m.tier == null || m.tier.numeric >= maxCompress || clay == null || compressed == null) {
                ItemHandlerHelper.insertItem(itemInventory, stack, false)
                continue
            }

            val compressedAmount = stack.count / 9
            if (compressedAmount <= 0) {
                ItemHandlerHelper.insertItem(itemInventory, stack, false)
                continue
            }

            val compressedStack = OreDictUnifier.get(OrePrefix.block, compressed, compressedAmount)
            val remain0 = ItemHandlerHelper.insertItem(itemInventory, compressedStack, true)
            // simulate is set to true above,
            // so grow(64) to virtually reproduce the (simulate = false) situation
            val stack1 = stack.copy().apply { shrink(compressedAmount * 9); grow(64) }
            val remain1 = ItemHandlerHelper.insertItem(itemInventory, stack1, true)
            if (remain0.isEmpty && remain1.isEmpty) {
                ItemHandlerHelper.insertItem(itemInventory, compressedStack, false)
                stack.shrink(compressedAmount * 9)
                ItemHandlerHelper.insertItem(itemInventory, stack, false)
            } else {
                ItemHandlerHelper.insertItem(itemInventory, stack, false)
            }
        }
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemInventory)
            val i = facing.index
            val inputSlots = when (inputModes[i]) {
                ALL -> createFilteredItemHandler(importItems, facing)
                else -> null
            }
            val outputSlots = when (outputModes[i]) {
                ALL -> createFilteredItemHandler(FilteredItemHandler(exportItems) { itemStack ->
                    val maxCompress = maxCompressedClay.getStackInSlot(0)
                    maxCompress.isEmpty || maxCompress.canStackWith(itemStack)
                }, facing)
                else -> null
            }
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(ItemHandlerProxy(inputSlots, outputSlots))
        }
        return super.getCapability(capability, facing)
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setTag("maxCompressedClay", maxCompressedClay.serializeNBT())
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        maxCompressedClay.deserializeNBT(data.getCompoundTag("maxCompressedClay"))
    }
}