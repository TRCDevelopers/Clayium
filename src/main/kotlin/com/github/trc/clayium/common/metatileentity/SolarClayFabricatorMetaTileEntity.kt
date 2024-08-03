package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.Widget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.ClayFabricatorRecipeLogic
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.AutoIoHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils.clayiumId
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.recipe.builder.ClayFabricatorRecipeBuilder
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class SolarClayFabricatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    val registry: RecipeRegistry<ClayFabricatorRecipeBuilder>
) : MetaTileEntity(metaTileEntityId, tier,
    validInputModes, validOutputModesLists[1], "machine.${CValues.MOD_ID}.solar_clay_fabricator.${tier.lowerName}") {

    override val faceTexture: ResourceLocation = clayiumId("blocks/solar")

    override val importItems: IItemHandlerModifiable = NotifiableItemStackHandler(this, 1, this, false)
    override val exportItems: IItemHandlerModifiable = NotifiableItemStackHandler(this, 1, this, true)
    override val itemInventory: IItemHandler = ItemHandlerProxy(importItems, exportItems)
    val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    private val workable = SolarClayFabricatorRecipeLogic()

    override fun createMetaTileEntity(): MetaTileEntity {
        return SolarClayFabricatorMetaTileEntity(metaTileEntityId, tier, registry)
    }

    override fun isFacingValid(facing: EnumFacing): Boolean {
        return facing == EnumFacing.UP
    }

    override fun onPlacement() {
        this.frontFacing = EnumFacing.UP
        super.onPlacement()
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("solar_clay_fabricator"), "tier=${tier.lowerName}"))
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        val panel = ModularPanel.defaultPanel(this.metaTileEntityId.toString())

        panel.child(IKey.lang("machine.${CValues.MOD_ID}.solar_clay_fabricator").asWidget()
            .top(6)
            .left(6))

        val slotsAndProgressBar = Row()
            .widthRel(0.7f).height(26)
            .align(Alignment.Center)
            .top(30)
            .child(workable.getProgressBar(syncManager).align(Alignment.Center))

        slotsAndProgressBar.child(Widget()
            .size(26, 26).left(4)
            .background(ClayGuiTextures.LARGE_SLOT))
            .child(ItemSlot().left(8).top(4)
                .slot(SyncHandlers.itemSlot(importItems, 0)
                    .singletonSlotGroup(2))
                .background(IDrawable.EMPTY))
        slotsAndProgressBar.child(Widget()
            .size(26, 26).right(4)
            .background(ClayGuiTextures.LARGE_SLOT))
            .child(ItemSlot().right(8).top(4)
                .slot(SyncHandlers.itemSlot(exportItems, 0)
                    .accessibility(false, true)
                    .singletonSlotGroup(0))
                .background(IDrawable.EMPTY))

        panel.child(slotsAndProgressBar)
        panel.child(workable.createCeTextWidget(syncManager)
            .widthRel(0.3f)
            .pos(6, 60))
            .child(playerInventoryTitle())

        return panel.bindPlayerInventory()
    }

    private inner class SolarClayFabricatorRecipeLogic : ClayFabricatorRecipeLogic(this@SolarClayFabricatorMetaTileEntity, registry) {
        override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
            if (simulate) return true
            val pos = pos ?: return false
            return world?.canSeeSky(pos.up()) == true && super.drawEnergy(ce, simulate)
        }
    }

    companion object {
        private val validInputModes = listOf(MachineIoMode.NONE, MachineIoMode.ALL)
    }
}