package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Flow
import io.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.client.model.ModelTextures
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.function.Function

class AutoCrafterMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    private val useEnergy: Boolean = true,
) : MetaTileEntity(
    metaTileEntityId, tier,
    if (useEnergy) validInputModesLists[1] else bufferValidInputModes,
    validOutputModesLists[0],
    "auto_crafter",
) {
    override val importItems = ClayiumItemStackHandler(this, 9)
    override val exportItems = ClayiumItemStackHandler(this, 6)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    private val sampleCraftingGrid = ClayiumItemStackHandler(this, 9)

    private val clayEnergyHolder = if (useEnergy) ClayEnergyHolder(this) else null

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        CUtils.writeItems(sampleCraftingGrid, "sample_crafting_grid", data)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        CUtils.readItems(sampleCraftingGrid, "sample_crafting_grid", data)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return AutoCrafterMetaTileEntity(metaTileEntityId, tier, useEnergy)
    }

    private fun isItemValidForCraftingGrid(index: Int, itemStack: ItemStack): Boolean {
        if (itemStack.isEmpty) return true
        val sampleItem = sampleCraftingGrid.getStackInSlot(index)
        val filterCapability = sampleItem.getCapability(ClayiumCapabilities.ITEM_FILTER, null)
        return filterCapability?.test(itemStack)
            ?: (sampleItem.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(sampleItem, itemStack))
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(Flow.row().height(18 * 3).widthRel(1f).align(Alignment.Center)
                .child(SlotGroupWidget.builder()
                    .matrix("SSS", "SSS", "SSS")
                    .key('S') { MuiSlots.phantomSlot(sampleCraftingGrid, it) }.build()
                    .background(ClayGuiTextures.FILTER_SLOT)
                    .marginLeft(0)
                )
                .child(SlotGroupWidget.builder()
                    .matrix("III", "III", "III")
                    .key('I') { i ->
                        MuiSlots.itemSlotBuilder(importItems, i)
                            .filter { stack -> isItemValidForCraftingGrid(i, stack) }
                            .build()
                    }.build()
                    .alignY(Alignment.Center).marginLeft(4)
                )
                .child(SlotGroupWidget.builder()
                    .matrix("OO", "OO", "OO")
                    .key('O') { MuiSlots.itemSlotBuilder(exportItems, it).takeOnly().build() }
                    .build()
                    .align(Alignment.CenterRight)
                )
            )
    }

    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val sideAtlas = getter.apply(clayiumId("blocks/auto_crafter_side"))
        val topAtlas = getter.apply(clayiumId("blocks/auto_crafter_top"))
        sideQuads = (0..3).map(EnumFacing::byHorizontalIndex)
            .map { ModelTextures.createQuad(it, sideAtlas) }
        topQuad = ModelTextures.createQuad(EnumFacing.UP, topAtlas)
    }

    override fun getQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        super.getQuads(quads, state, side, rand)
        if (state == null || side == null || state !is IExtendedBlockState) return
        if (side == EnumFacing.UP) {
            quads.add(topQuad)
        } else if (side.axis.isHorizontal) {
            quads.add(sideQuads[side.horizontalIndex])
        }
    }

    companion object {
        private lateinit var sideQuads: List<BakedQuad>
        private lateinit var topQuad: BakedQuad
    }
}