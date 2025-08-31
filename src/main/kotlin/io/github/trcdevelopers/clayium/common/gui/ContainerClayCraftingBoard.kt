/*
 * heavily inspired by slimeknights.tconstruct.tools.common.inventory.ContainerCraftingStation
 */
package io.github.trcdevelopers.clayium.common.gui

import io.github.trcdevelopers.clayium.api.capability.impl.EmptyItemStackHandler
import io.github.trcdevelopers.clayium.common.blocks.claycraftingtable.TileClayCraftingBoard
import io.github.trcdevelopers.clayium.common.gui.slots.SlotCraftingCcb
import io.github.trcdevelopers.clayium.common.inventory.ItemHandlerWrappedInventoryCrafting
import io.github.trcdevelopers.clayium.common.network.CNetwork
import io.github.trcdevelopers.clayium.common.network.LastRecipePacket
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.network.play.server.SPacketSetSlot
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler
import java.util.stream.Collectors


private const val RESULT_SLOT_INDEX = 0

class ContainerClayCraftingBoard(
    private val player: EntityPlayer,
    private val world: World,
    val tile: TileClayCraftingBoard,
) : ContainerClayium() {

    val neighboringItemHandler = EnumFacing.entries.firstNotNullOfOrNull { facing ->
        tile.world.getTileEntity(tile.pos.offset(facing))
            ?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.opposite)
    }
    val hasNeighbor = neighboringItemHandler != null

    private val craftMatrix = ItemHandlerWrappedInventoryCrafting(tile.inventory, this, 3, 3)
    private val craftResult = InventoryCraftResult()

    var lastRecipe: IRecipe? = null
    private val playerInvIndexStart = if (neighboringItemHandler != null) 1 + 9 + neighboringItemHandler.slots else 1 + 9

    init {
        // SlotCrafting must be added first because [Container.onCraftMatrixChanged] will use hardcoded index 0 for result slot
        val slotCrafting = SlotCraftingCcb(player, this.craftMatrix, this.craftResult, 0, 124, 35, neighboringItemHandler ?: EmptyItemStackHandler, this)
        this.addSlotToContainer(slotCrafting)

        for (i in 0..<3) {
            for (j in 0..<3) {
                val slotIndex = i * 3 + j
                val slotX = (j * 18) + 29 + 1
                val slotY = (i * 18) + 16 + 1
                val slot = Slot(this.craftMatrix, slotIndex, slotX, slotY)
                this.addSlotToContainer(slot)
            }
        }

        val neighborSlotsY = 75
        if (this.neighboringItemHandler != null) {
            val rowSize = this.neighboringItemHandler.slots / 9

            for (i in 0..<rowSize) {
                if (i > 3) break
                for (j in 0..<9) {
                    val slotIndex = i * 9 + j
                    if (slotIndex >= this.neighboringItemHandler.slots) break
                    val slotX = (j * 18) + 7 + 1
                    val slotY = (i * 18) + neighborSlotsY + 1
                    val slot = SlotItemHandler(this.neighboringItemHandler, slotIndex, slotX, slotY)
                    this.addSlotToContainer(slot)
                }
            }
        }

        addPlayerSlots(this, player.inventory, if (hasNeighbor) 75 + 18*3 + 13 + 1 else 83 + 1)
        this.onCraftMatrixChanged(craftMatrix)
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return !playerIn.isSpectator
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        val slot = this.inventorySlots[index]
            ?.takeIf { it.hasStack }
            ?: return ItemStack.EMPTY

        val neighborInvSize = this.neighboringItemHandler?.slots ?: 0

        val slotStack = slot.stack
        val slotStackSnapshot = slotStack.copy()
        when (index) {
            RESULT_SLOT_INDEX -> {
                slotStack.item.onCreated(slotStack, this.world, playerIn)
                if (!this.mergeItemStack(slotStack, 10, this.inventorySlots.size, true)) {
                    return ItemStack.EMPTY
                }
                slot.onSlotChange(slotStack, slotStackSnapshot)
            }
            in 1..9 -> {
                // Craft Grid -> neighbor inv? -> player inv
                if (!(this.mergeItemStack(slotStack, 10, 10 + neighborInvSize, false)
                            || this.mergeItemStack(slotStack, playerInvIndexStart, playerInvIndexStart + 36, true))) {
                    return ItemStack.EMPTY
                }
            }
            in 10..<(10 + neighborInvSize) -> {
                // Neighbor inv -> craft grid? -> player inv
                if (!(this.mergeItemStack(slotStack, 1, 10, false)
                    || this.mergeItemStack(slotStack, playerInvIndexStart, playerInvIndexStart + 36, true))) {
                    return ItemStack.EMPTY
                }
            }
            in playerInvIndexStart..<(playerInvIndexStart + 36) -> {
                // Player inventory -> neighbor inv? -> craft grid
                if (!(this.mergeItemStack(slotStack, 10, 10 + neighborInvSize, false)
                            || this.mergeItemStack(slotStack, 1, 10, false))) {
                    return ItemStack.EMPTY
                }
            }
            else -> {
                if (!this.mergeItemStack(slotStack, 0, this.inventorySlots.size, false)) {
                    return ItemStack.EMPTY
                }
            }
        }
        if (slotStack.isEmpty) {
            slot.putStack(ItemStack.EMPTY)
        } else {
            slot.onSlotChanged()
        }
        if (slotStack.count == slotStackSnapshot.count) {
            return ItemStack.EMPTY
        }
        val stack2 = slot.onTake(playerIn, slotStack)
        if (index == RESULT_SLOT_INDEX) {
            player.dropItem(stack2, false)
        }
        return slotStackSnapshot
    }

    override fun onCraftMatrixChanged(inventoryIn: IInventory) {
        this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult)
    }

    override fun slotChangedCraftingGrid(world: World, player: EntityPlayer, craftMatrix: InventoryCrafting, craftResult: InventoryCraftResult) {
        var lastRecipe = this.lastRecipe

        // If lastRecipe is not null and matches, skip this if and fetch its result
        // otherwise, try to find a new matching recipe, and fetch its result
        if (lastRecipe == null || !lastRecipe.matches(craftMatrix, this.world)) {
            lastRecipe = CraftingManager.findMatchingRecipe(craftMatrix, world)
        }
        val itemStack = lastRecipe?.getCraftingResult(craftMatrix) ?: ItemStack.EMPTY

        if (!world.isRemote) {
            getAllPlayersOpeningThisContainer(world as WorldServer)
                .forEach {
                    it.openContainer.putStackInSlot(RESULT_SLOT_INDEX, itemStack)
                    it.connection.sendPacket(SPacketSetSlot(this.windowId, RESULT_SLOT_INDEX, itemStack))
                    CNetwork.channel.sendTo(LastRecipePacket(lastRecipe), it)
                }
        }

        craftResult.setInventorySlotContents(RESULT_SLOT_INDEX, itemStack)
    }

    override fun canMergeSlot(stack: ItemStack, slotIn: Slot): Boolean {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn)
    }

    private fun getAllPlayersOpeningThisContainer(worldServer: WorldServer): List<EntityPlayerMP> {
        return worldServer.playerEntities.stream()
            .filter { player -> player.openContainer is ContainerClayCraftingBoard }
            .filter { player -> (player.openContainer as ContainerClayCraftingBoard).tile == this.tile }
            .map { player -> player as EntityPlayerMP }
            .collect(Collectors.toList())
    }
}