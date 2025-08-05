package com.github.trc.clayium.common.items

import baubles.api.BaubleType
import baubles.api.IBauble
import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.HandGuiData
import com.cleanroommc.modularui.factory.ItemGuiFactory
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.screen.UISettings
import com.cleanroommc.modularui.utils.ItemStackItemHandler
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Flow
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IItemGadget
import com.github.trc.clayium.api.capability.ItemCapabilityProvider
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.util.UtilLocale
import com.github.trc.clayium.integration.baubles.BaubleClayGadgets
import com.github.trc.clayium.integration.modularui.MuiSlots
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import java.util.*

@Optional.Interface(iface = "baubles.api.IBauble", modid = Mods.Names.BAUBLES)
class ItemClayGadgetHolder : Item(), IGuiHolder<HandGuiData>, IBauble {
    init {
        maxStackSize = 1
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack?> {
        if (!worldIn.isRemote) {
            ItemGuiFactory.INSTANCE.open(playerIn as EntityPlayerMP, handIn)
        }
        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))
    }

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)
        if (entityIn !is EntityPlayer) return
        val handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            ?: return
        for (i in 0..<handler.slots) {
            val stack = handler.getStackInSlot(i)
            val gadget = stack.getCapability(ClayiumCapabilities.CLAY_GADGET, null)
            gadget?.updateInventory(entityIn, worldIn.isRemote)
        }
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        UtilLocale.formatTooltips(tooltip, "clayium.clay_gadget_holder.tooltip")
    }

    override fun buildUI(data: HandGuiData, syncManager: PanelSyncManager, settings: UISettings): ModularPanel {
        syncManager.registerSlotGroup("clayium_gadget_holder", 2)

        val stack = data.usedItemStack
        val itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as? IItemHandlerModifiable
            ?: return ModularPanel.defaultPanel("simple_item_filter_error")

        val previousGadgets = Array(itemHandler.slots) {
            itemHandler.getStackInSlot(it).getCapability(ClayiumCapabilities.CLAY_GADGET, null)
        }
        MuiSlots.lockHeldItem(syncManager, data.player)
        return ModularPanel.defaultPanel("clayium:gadget_holder", 176, 150)
            .child(Flow.column().margin(7).sizeRel(1f)
                .child(IKey.str(stack.displayName).asWidget().left(0))
                .child(SlotGroupWidget.builder()
                    .matrix("IIIII", "IIIII")
                    .key('I') {
                        MuiSlots.itemSlotBuilder(itemHandler, it).slotGroup("clayium_gadget_holder")
                            .filter { target ->
                                val gadget = target.getCapability(ClayiumCapabilities.CLAY_GADGET, null)
                                if (gadget == null) return@filter false
                                for (slot in 0..<itemHandler.slots) {
                                    if (itemHandler.getStackInSlot(slot).isEmpty) continue
                                    val otherGadget = itemHandler.getStackInSlot(slot).getCapability(ClayiumCapabilities.CLAY_GADGET, null)
                                    if (otherGadget != null && otherGadget.category == gadget.category) {
                                        return@filter false
                                    }
                                }
                                return@filter true
                            }
                            .changeListener { newStack, onlyAmountChanged, client, init ->
                                if (client) return@changeListener
                                if (newStack.isEmpty) {
                                    previousGadgets[it]?.removeFromHolder(data.player)
                                } else {
                                    val gadget = newStack.getCapability(ClayiumCapabilities.CLAY_GADGET, null)
                                    gadget?.putInHolder(data.player)
                                }
                            }
                            .build()
                    }
                    .build().marginTop(2))
                .child(IKey.lang("container.inventory").asWidget().left(0).marginTop(2).marginBottom(1))
                .child(MuiSlots.playerInventory(0))
            )
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        val superProvider = super.initCapabilities(stack, nbt)
        return object : ItemCapabilityProvider {
            override fun <T> getCapability(capability: Capability<T>): T? {
                return if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                    return capability.cast(ItemStackItemHandler(stack, 5 * 2))
                } else {
                    superProvider?.getCapability(capability, null)
                }
            }
        }
    }

    @Optional.Method(modid = Mods.Names.BAUBLES)
    override fun getBaubleType(itemstack: ItemStack?): BaubleType? {
        return BaubleType.TRINKET
    }

    @Optional.Method(modid = Mods.Names.BAUBLES)
    override fun onWornTick(itemstack: ItemStack, player: EntityLivingBase) {
        if (player !is EntityPlayer) return
        val handler = itemstack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            ?: return
        for (i in 0..<handler.slots) {
            val stack = handler.getStackInSlot(i)
            val gadget = stack.getCapability(ClayiumCapabilities.CLAY_GADGET, null)
            gadget?.updateInventory(player, player.world.isRemote)
        }
    }

    companion object {
        private val playerToGadgets = mutableMapOf<UUID, Set<IItemGadget>>()

        fun hasGadget(player: UUID, gadget: IItemGadget): Boolean {
            return playerToGadgets[player]?.contains(gadget) ?: false
        }

        fun onPlayerLogin(player: EntityPlayer) {
            getGadgets(player).forEach { it.onLogin(player) }
        }

        fun onPlayerLogout(player: EntityPlayer) {
            getGadgets(player).forEach { it.onLogout(player) }
        }

        fun onTick(player: EntityPlayer) {
            if (player is EntityPlayerMP) {
                val previous = playerToGadgets[player.uniqueID]
                    ?: emptySet()
                val current = getGadgets(player).toSet()

                val newGadgets = current - previous
                val removedGadgets = previous - current
                for (gadget in newGadgets) {
                    gadget.putInHolder(player)
                }

                for (gadget in removedGadgets) {
                    gadget.removeFromHolder(player)
                }
                playerToGadgets[player.uniqueID] = current
            }
        }

        private fun getGadgets(player: EntityPlayer): List<IItemGadget> {
            val gadgets = mutableListOf<IItemGadget>()
            searchGadgetHolder(player).forEach { holder ->
                val handler = holder.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
                    ?: return@forEach
                for (j in 0..<handler.slots) {
                    val gadget = handler.getStackInSlot(j).getCapability(ClayiumCapabilities.CLAY_GADGET, null)
                        ?: continue
                    gadgets.add(gadget)
                }
            }
            return gadgets
        }

        private fun searchGadgetHolder(player: EntityPlayer): List<ItemStack> {
            val gadgetHolders = mutableListOf<ItemStack>()
            if (Mods.Baubles.isModLoaded) {
                val maybeHolder = BaubleClayGadgets.searchGadgetHolderInBaubles(player)
                if (!maybeHolder.isEmpty) gadgetHolders.add(maybeHolder)
            }
            for (i in 0..<player.inventory.sizeInventory) {
                val stack = player.inventory.getStackInSlot(i)
                if (stack.item == ClayiumItems.CLAY_GADGET_HOLDER) {
                    gadgetHolders.add(stack)
                }
            }
            return gadgetHolders
        }
    }
}