package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IControllable
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.AutoIoHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.pan.IPan
import com.github.trc.clayium.api.pan.IPanCable
import com.github.trc.clayium.api.pan.IPanUser
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.recipe.ingredient.COreRecipeInput
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix
import com.github.trc.clayium.common.unification.stack.ItemAndMeta
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import org.apache.commons.lang3.ObjectUtils
import kotlin.math.pow

class PanDuplicatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    private val duplicatorRank: Int,
) : MetaTileEntity(metaTileEntityId, tier, validInputModesLists[2], validOutputModesLists[1],
    "machine.${CValues.MOD_ID}.pan_duplicator"), IPanUser {

    private val ceConsumption = ClayEnergy(10_000 * 10.0.pow(duplicatorRank - 1).toLong())

    private val antimatterSlot = NotifiableItemStackHandler(this, 1, this, isExport = false)
    private val duplicationTargetSlot = NotifiableItemStackHandler(this, 1, this, isExport = true)

    override val importItems = CombinedInvWrapper(antimatterSlot, duplicationTargetSlot)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)

    @Suppress("unused")
    private val ioHandler = AutoIoHandler.Combined(this)
    private val clayEnergyHolder = ClayEnergyHolder(this)
    private val recipeLogic = DuplicatorRecipeLogic()

    private var pan: IPan? = null

    override fun update() {
        super.update()
        recipeLogic.update()
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.PAN_CABLE -> capability.cast(IPanCable.INSTANCE)
            capability === ClayiumTileCapabilities.PAN_USER -> capability.cast(this)
            else -> super.getCapability(capability, facing)
        }
    }

    override fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        super.clearMachineInventory(itemBuffer)
        clearInventory(itemBuffer, clayEnergyHolder.energizedClayItemHandler)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return PanDuplicatorMetaTileEntity(metaTileEntityId, tier, duplicatorRank)
    }

    override fun registerItemModel(item: Item, meta: Int) {
    }

    @SideOnly(Side.CLIENT)
    override fun getItemStackDisplayName(): String {
        return I18n.format(this.translationKey, this.duplicatorRank)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        tooltip.add("CE Consumption Rate: ${ceConsumption.format()}/t")
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("pan_duplicator")
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(clayEnergyHolder.createCeTextWidget(syncManager)
                        .bottom(12).left(0).widthRel(0.5f))
                    .child(Row().widthRel(0.7f).height(26).align(Alignment.Center)
                        .child(SlotGroupWidget.builder()
                            .row("AD")
                            .key('A', ItemSlot().slot(SyncHandlers.itemSlot(antimatterSlot, 0).singletonSlotGroup())
                                .background(ClayGuiTextures.IMPORT_1_SLOT))
                            .key('D', ItemSlot().slot(SyncHandlers.itemSlot(duplicationTargetSlot, 0).singletonSlotGroup())
                                .background(ClayGuiTextures.IMPORT_2_SLOT))
                            .build()
                            .align(Alignment.CenterLeft)
                        )
                        .child(largeSlot(SyncHandlers.itemSlot(exportItems, 0).singletonSlotGroup().accessibility(false, true))
                            .align(Alignment.CenterRight))
                        .child(ProgressWidget()
                            .progress(0.0)
                            .size(22, 17).align(Alignment.Center)
                            .texture(ClayGuiTextures.PROGRESS_BAR, 22)
                        )
                    )
                )
                .child(SlotGroupWidget.playerInventory(0))
            )
    }

    override fun setNetwork(network: IPan) {
        pan = network
    }

    override fun resetNetwork() {
        pan = null
    }

    private inner class DuplicatorRecipeLogic : IControllable {

        override var isWorkingEnabled = true
        override val isWorking
            get() = isWorkingEnabled && targetItem != null

        private var energyRequired = ClayEnergy.ZERO
        private var currentEnergy = ClayEnergy.ZERO
        private var targetItem: ItemAndMeta? = null

        private var outputFullLastTime = false
        private var inputInvalidLastTime = false

        fun update() {
            if (isRemote || !isWorkingEnabled) return
            if (targetItem != null) {
                updateProgress()
            }
            if (targetItem == null && shouldSearchForDuplication()) {
                val duplicationEntry = trySearchDuplicationEntry()
                if (duplicationEntry != null) {
                    targetItem = duplicationEntry.first
                    energyRequired = duplicationEntry.second
                    antimatterSlot.extractItem(0, 1, false)
                }
            }
        }

        fun updateProgress() {
            if (currentEnergy >= energyRequired) {
                exportItems.insertItem(0, targetItem!!.asStack(), false)
            } else {
                val maxConsume = ceConsumption
                val energyRequiredLeft = energyRequired - currentEnergy
                val consume = ObjectUtils.min(maxConsume, energyRequiredLeft)
                if (clayEnergyHolder.drawEnergy(consume, false)) {
                    currentEnergy += consume
                }
            }
        }

        private fun shouldSearchForDuplication(): Boolean {
            if ((inputInvalidLastTime && !hasNotifiedInputs)
                || outputFullLastTime && !hasNotifiedOutputs) return false

            inputInvalidLastTime = false
            hasNotifiedInputs = false
            outputFullLastTime = false
            hasNotifiedOutputs = false
            return true
        }

        fun trySearchDuplicationEntry(): Pair<ItemAndMeta, ClayEnergy>? {
            if (!antimatterInput.testItemStackAndAmount(antimatterSlot.getStackInSlot(0))) return null
            val targetStack = duplicationTargetSlot.getStackInSlot(0)
            if (targetStack.isEmpty) return null
            val input = ItemAndMeta(duplicationTargetSlot.getStackInSlot(0))
            val energy = pan?.getDuplicationEntries()[input] ?: return null
            antimatterSlot.extractItem(0, 1, false)
            return input to energy
        }
    }

    companion object {
        private val antimatterInput = COreRecipeInput(OrePrefix.gem, CMaterials.antimatter)
    }
}