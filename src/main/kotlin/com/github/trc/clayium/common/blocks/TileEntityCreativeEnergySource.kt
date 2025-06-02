package com.github.trc.clayium.common.blocks

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.capability.impl.InfiniteItemStackHandler
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.api.util.toItemStack
import com.github.trc.clayium.common.blocks.material.BlockEnergizedClay
import com.github.trc.clayium.integration.modularui.IGuiHolderClayium
import com.github.trc.clayium.integration.modularui.MuiSlots
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler

class TileEntityCreativeEnergySource : TileEntity(), IGuiHolderClayium<PosGuiData> {

    private val handler by lazy {
        var highest: IBlockState? = null
        for (block in ClayiumBlocks.ENERGIZED_CLAY_BLOCKS) {
            for (state in block.blockState.validStates) {
                if (highest == null) highest = state
                val matCe = block.getCMaterial(state).getProperty(CPropertyKey.CLAY).energy
                val currentCe = (highest.block as BlockEnergizedClay).getCMaterial(highest).getProperty(CPropertyKey.CLAY).energy!!
                if (matCe == null) continue
                if (matCe > currentCe) {
                    highest = state
                }
            }
        }
        InfiniteItemStackHandler(highest!!.toItemStack(64))
    }

    override fun buildUI(data: PosGuiData, syncManager: PanelSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("clayium:creative_energy_source")
            .child(Column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang("tile.clayium.creative_energy_source.name").asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget().align(Alignment.BottomLeft))
                    .child(MuiSlots.itemSlotBuilder(handler, 0).takeOnly().build()
                        .align(Alignment.Center))
                )
                .child(MuiSlots.playerInventory(0))
            )
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        return if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handler)
        } else {
            super.getCapability(capability, facing)
        }
    }
}