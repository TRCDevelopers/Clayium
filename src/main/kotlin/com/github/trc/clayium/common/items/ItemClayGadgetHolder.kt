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
import com.github.trc.clayium.api.capability.ClayiumPlayerData
import com.github.trc.clayium.api.capability.IItemGadget
import com.github.trc.clayium.api.capability.ItemCapabilityProvider
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.api.util.clayiumId
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
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

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
        return ModularPanel.defaultPanel("clayium:gadget_holder").height(144 + 18 * 2)
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
                .child(IKey.lang("container.inventory").asWidget().left(0).marginTop(2))
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
        val handler = itemstack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            ?: return
        for (i in 0..<handler.slots) {
            val stack = handler.getStackInSlot(i)
            val gadget = stack.getCapability(ClayiumCapabilities.CLAY_GADGET, null)
            gadget?.updateInventory(player, player.world.isRemote)
        }
    }

    companion object {
        @SubscribeEvent(priority = EventPriority.LOW) // execute after the baubles' one, so baubles slots are synced
        fun onPlayerLogin(event: EntityJoinWorldEvent) {
            val entity = event.entity
            if (entity is EntityPlayer) {
                getGadgets(entity).forEach { it.onLogin(entity) }
            }
        }

        @SubscribeEvent
        fun onPlayerLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
            val player = event.player
            getGadgets(player).forEach { it.onLogout(player) }
        }

        private fun getGadgets(player: EntityPlayer): List<IItemGadget> {
            val playerInventory = player.inventory
            val gadgets = mutableListOf<IItemGadget>()
            if (Mods.Baubles.isModLoaded) {
                BaubleClayGadgets.getBaubleGadgets(gadgets, player)
            }
            for (i in 0..<playerInventory.sizeInventory) {
                val stack =  playerInventory.getStackInSlot(i)
                if (stack.item != ClayiumItems.CLAY_GADGET_HOLDER) continue

                val handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
                    ?: continue
                for (j in 0..<handler.slots) {
                    handler.getStackInSlot(j).getCapability(ClayiumCapabilities.CLAY_GADGET, null)
                        ?.let { gadgets.add(it) }
                }
            }
            return gadgets
        }

        @SubscribeEvent
        fun onAttachCapabilityEntity(e: AttachCapabilitiesEvent<Entity>) {
            val player = e.`object`
            if (player is EntityPlayer) {
                e.addCapability(clayiumId("player_data"), ClayiumPlayerData())
            }
        }
    }
}