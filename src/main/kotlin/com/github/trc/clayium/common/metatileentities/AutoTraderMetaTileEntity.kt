package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.drawable.DynamicDrawable
import com.cleanroommc.modularui.drawable.UITexture
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.DoubleValue
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.GUI_DEFAULT_HEIGHT
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.recipe.IRecipeProvider
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.toList
import com.github.trc.clayium.common.config.ConfigCore
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.gui.sync.MerchantRecipeListSyncValue
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.ingredient.CItemRecipeInput
import com.github.trc.clayium.common.recipe.ingredient.CRecipeInput
import net.minecraft.entity.IMerchant
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.village.MerchantRecipe
import net.minecraft.village.MerchantRecipeList
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.items.IItemHandlerModifiable
import java.lang.ref.WeakReference

private val EMPTY_MLIST = MerchantRecipeList()
private val TRADE_DISABLED: UITexture = UITexture.builder().location(ResourceLocation("textures/gui/container/villager.png"))
    .uv(212, 0, 28, 21)
    .build()

class AutoTraderMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, validInputModesLists[2], validOutputModesLists[1], "auto_trader") {
    override val importItems = NotifiableItemStackHandler(this, 2, this, isExport = false)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)

    private val clayEnergyHolder = ClayEnergyHolder(this)

    private var weakRefFakePlayer = WeakReference<FakePlayer>(null)
    /** Only available on the server side */
    private val fakePlayer: FakePlayer
        get() {
            val current = weakRefFakePlayer.get()
            if (current != null) return current
            val newFakePlayer = CUtils.getFakePlayer(this.world as WorldServer)
            weakRefFakePlayer = WeakReference(newFakePlayer)
            return newFakePlayer
        }
    private var merchant: IMerchant? = null
    private var trades: MerchantRecipeList? = null
    private var tradeIndex: Int = 0
    private val trade: MerchantRecipe?
        get() {
            val trades = this.trades
                ?.takeUnless { it.isEmpty }
                ?: return null
            if (trades.size <= this.tradeIndex) {
                this.tradeIndex = 0
            }
            return trades[tradeIndex]?.takeUnless { it.isRecipeDisabled }
        }

    private val tradePreviewItemHandler = TradePreviewItemHandler()

    private val recipeLogic = AutoTraderRecipeLogic()

    override fun update() {
        super.update()
        if (isRemote || offsetTimer % 5 != 0L) return
        val world = world ?: return
        val pos = pos ?: return

        val entities = world.getEntitiesWithinAABB(EntityVillager::class.java, AxisAlignedBB(pos, pos.add(1, 3, 1)))
            .filter { !it.isChild }
        if (entities.isEmpty()) {
            this.trades = EMPTY_MLIST
            this.merchant = null
        } else {
            val merchant = entities.first()
            this.trades = merchant.getRecipes(fakePlayer)
            this.merchant = merchant
        }
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel(translationKey, GUI_DEFAULT_WIDTH, GUI_DEFAULT_HEIGHT + 22)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        syncManager.syncValue("trade", MerchantRecipeListSyncValue({ this.trades ?: EMPTY_MLIST }, { this.trades = it }))
        val previewProgressBar = DynamicDrawable {
            val trade = this.trade
            if (trade == null || trade.isRecipeDisabled) {
                TRADE_DISABLED
            } else {
                ClayGuiTextures.PROGRESS_BAR
            }
        }
        return super.buildMainParentWidget(syncManager)
            .child(Column().widthRel(0.9f).coverChildrenHeight().alignX(0.5f).top(16)
                .child(Row().widthRel(1f).height(17).debugName("Preview Row")
                    .alignX(0.5f)
                    .child(ItemSlot().alignY(0.5f).marginLeft(0).background(IDrawable.EMPTY)
                        .slot(SyncHandlers.itemSlot(tradePreviewItemHandler, 0)
                            .accessibility(false, false))
                    )
                    .child(ItemSlot().alignY(0.5f).marginLeft(9).background(IDrawable.EMPTY)
                        .slot(SyncHandlers.itemSlot(tradePreviewItemHandler, 1)
                            .accessibility(false, false))
                    )
                    .child(
                        ProgressWidget().size(22, 17).align(Alignment.Center)
                        .value(DoubleValue(0.0))
                        .texture(ClayGuiTextures.PROGRESS_BAR, 22)
                    )
                    .child(ItemSlot().right(4).alignY(0.5f).background(IDrawable.EMPTY)
                        .slot(SyncHandlers.itemSlot(tradePreviewItemHandler, 2)
                            .accessibility(false, false))
                    )
                )
                .child(Row().widthRel(1f).height(26).alignX(0.5f).marginTop(6)
                    .debugName("Inventory Row")
                    .child(ItemSlot().alignY(0.5f).marginLeft(0).background(ClayGuiTextures.IMPORT_1_SLOT)
                        .slot(SyncHandlers.itemSlot(importItems, 0).singletonSlotGroup(100))
                    )
                    .child(ItemSlot().alignY(0.5f).marginLeft(9).background(ClayGuiTextures.IMPORT_2_SLOT)
                        .slot(SyncHandlers.itemSlot(importItems, 1).singletonSlotGroup(101))
                    )
                    .child(recipeLogic.getProgressBar(syncManager, showRecipes = false)
                        .align(Alignment.Center))
                    .child(largeSlot(SyncHandlers.itemSlot(exportItems, 0)
                        .accessibility(false, true))
                        .right(0)
                    )
                )
            )
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return AutoTraderMetaTileEntity(metaTileEntityId, tier)
    }

    private inner class AutoTraderRecipeLogic : RecipeLogicEnergy(this@AutoTraderMetaTileEntity, AutoTraderRecipeProvider(), clayEnergyHolder) {

        override fun trySearchNewRecipe() {
            // don't use a cached recipe because it may run out of stock
            val recipe = recipeProvider.searchRecipe(getTier(), inputInventory.toList())
            if (recipe == null) {
                invalidInputsForRecipes = true
                this.isWorking = false
                return
            }
            this.isWorking = prepareRecipe(recipe)
        }

        override fun completeWork() {
            merchant?.useRecipe(trade!!)
            super.completeWork()
        }
    }

    private inner class AutoTraderRecipeProvider : IRecipeProvider {
        override val jeiCategory: String? = null

        override fun searchRecipe(machineTier: Int, inputs: List<ItemStack>): Recipe? {
            val trade = trade
            if (trade == null) return null
            val cet = ClayEnergy.of(ConfigCore.misc.autoTraderEnergyConsumption.toLong())
            val input = trade.itemToBuy
            val secondaryInput = trade.secondItemToBuy
            val output = trade.itemToSell

            if (output.isEmpty || (input.isEmpty && secondaryInput.isEmpty)) return null

            val recipeInputs = mutableListOf<CRecipeInput>()
            if (!input.isEmpty) recipeInputs.add(CItemRecipeInput(input))
            if (!secondaryInput.isEmpty) recipeInputs.add(CItemRecipeInput(secondaryInput))
            val outputs = mutableListOf(output)
            return Recipe(
                recipeInputs, outputs,
                duration = ConfigCore.misc.autoTraderRecipeDurationTick.toLong(),
                cePerTick = cet,
                recipeTier = 0,
            )
        }
    }

    private inner class TradePreviewItemHandler : IItemHandlerModifiable by EmptyItemStackHandler {
        override fun getSlots() = 3
        override fun getSlotLimit(slot: Int) = 64

        override fun getStackInSlot(slot: Int): ItemStack {
            val trade = trade
            if (trade == null) return ItemStack.EMPTY
            return when (slot) {
                0 -> trade.itemToBuy
                1 -> trade.secondItemToBuy
                2 -> trade.itemToSell
                else -> ItemStack.EMPTY
            }
        }
    }
}