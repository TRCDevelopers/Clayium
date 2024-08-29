package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.TextWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.clayiumId
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

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(Row().widthRel(0.7f).height(26).align(Alignment.Center)
                .child(largeSlot(SyncHandlers.itemSlot(importItems, 0)
                    .singletonSlotGroup(2)).align(Alignment.CenterLeft))
                .child(workable.getProgressBar(syncManager).align(Alignment.Center))
                .child(largeSlot(SyncHandlers.itemSlot(exportItems, 0)
                    .accessibility(false, true)
                    .singletonSlotGroup(0)).align(Alignment.CenterRight))
            )
            .child(workable.createCeTextWidget(syncManager)
                .bottom(12).left(0).widthRel(0.5f))
    }

    private inner class SolarClayFabricatorRecipeLogic : AbstractRecipeLogic(this@SolarClayFabricatorMetaTileEntity, registry) {
        private var clayEnergy = ClayEnergy.ZERO

        override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
            val world = world ?: return false
            val pos = pos ?: return false
            if (!world.canSeeSky(pos.up())) return false

            if (simulate) return true
            clayEnergy += ce
            return true
        }

        override fun completeWork() {
            clayEnergy = ClayEnergy.ZERO
            super.completeWork()
        }

        fun createCeTextWidget(syncManager: GuiSyncManager): TextWidget {
            syncManager.syncValue("clayEnergy", SyncHandlers.longNumber(
                { clayEnergy.energy },
                { clayEnergy = ClayEnergy(it) }
            ))

            return IKey.dynamic { clayEnergy.format() }.asWidget()
        }
    }

    companion object {
        private val validInputModes = listOf(MachineIoMode.NONE, MachineIoMode.ALL)
    }
}