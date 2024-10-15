package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.drawable.DynamicDrawable
import com.cleanroommc.modularui.drawable.ItemDrawable
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.recipe.IRecipeProvider
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.gui.sync.MerchantRecipeListSyncValue
import com.github.trc.clayium.common.recipe.Recipe
import net.minecraft.entity.IMerchant
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.village.MerchantRecipe
import net.minecraft.village.MerchantRecipeList
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayer
import java.lang.ref.WeakReference

private val EMPTY_MLIST = MerchantRecipeList()

class AutoTraderMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, validInputModesLists[2], validOutputModesLists[1], "auto_trader") {
    override val importItems = NotifiableItemStackHandler(this, 2, this, isExport = false)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)

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
            return trades[tradeIndex]
        }

    override fun update() {
        super.update()
        if (isRemote || offsetTimer % 5 != 0L) return
        val world = world ?: return
        val pos = pos ?: return

        val entities = world.getEntitiesWithinAABB(EntityVillager::class.java, AxisAlignedBB(pos, pos.add(1, 3, 1)))
        if (entities.isEmpty()) return

        this.trades = entities.first().getRecipes(fakePlayer)
        println(trades)
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        syncManager.syncValue("trades", MerchantRecipeListSyncValue({ this.trades ?: EMPTY_MLIST }, { this.trades = it }))
        val input1 = DynamicDrawable {
            val inputs = getTradeInputs()
            if (inputs.isEmpty()) ItemDrawable.EMPTY else ItemDrawable(getTradeInputs()[0])
        }
        val input2 = DynamicDrawable {
            val inputs = getTradeInputs()
            if (inputs.isEmpty()) ItemDrawable.EMPTY else ItemDrawable(getTradeInputs()[1])
        }
        val output = DynamicDrawable { ItemDrawable(getTradeOutput()) }
        return super.buildMainParentWidget(syncManager)
            .child(Row().widthRel(0.9f).coverChildrenHeight()
                .child(input1.asWidget().align(Alignment.CenterLeft))
                .child(input2.asWidget().marginLeft(16 + 10))
                .child(output.asWidget().align(Alignment.CenterRight))
            )
    }

    private fun getTradeInputs(): List<ItemStack> {
        return trade?.let {
            listOf(it.itemToBuy, it.secondItemToBuy)
        } ?: emptyList()
    }

    private fun getTradeOutput(): ItemStack {
        return this.trade?.itemToSell ?: ItemStack.EMPTY
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return AutoTraderMetaTileEntity(metaTileEntityId, tier)
    }

    private inner class AutoTraderRecipeProvider(merchant: IMerchant) : IRecipeProvider {
        override val jeiCategory: String? = null

        override fun searchRecipe(machineTier: Int, inputs: List<ItemStack>): Recipe? {
            TODO("Not yet implemented")
        }
    }
}