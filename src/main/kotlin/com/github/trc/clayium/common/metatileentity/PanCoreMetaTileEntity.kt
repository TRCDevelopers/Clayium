package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widget.ScrollWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Grid
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.client.model.ModelTextures
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import java.util.function.Function

class PanCoreMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, onlyNoneList, onlyNoneList, "${CValues.MOD_ID}.pan_core") {
    override val importItems = EmptyItemStackHandler
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = EmptyItemStackHandler

    override fun createMetaTileEntity(): MetaTileEntity {
        return PanCoreMetaTileEntity(metaTileEntityId, tier)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("pan_core"), "tier=${tier.lowerName}"))
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val tex = getter.apply(clayiumId("blocks/pan_core"))
        panCoreQuads = EnumFacing.entries.map { ModelTextures.createQuad(it, tex) }.toMutableList()
    }

    @SideOnly(Side.CLIENT)
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long) = panCoreQuads

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        val displayItems = Grid.mapToMatrix(8, panItems) { index, itemStack ->
            handler.setStackInSlot(index, itemStack)
            ItemSlot().slot(ModularSlot(handler, index).accessibility(false, false))
                .background(IDrawable.EMPTY)
        }
        return ModularPanel.defaultPanel("pan_core")
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(Grid().width(18 * 8 + 4).heightRel(1f)
                        .minElementMargin(0, 0)
                        .matrix(displayItems)
                        .scrollable()
                    )
                )
                .child(SlotGroupWidget.playerInventory(0)))
    }

    companion object {
        private lateinit var panCoreQuads: MutableList<BakedQuad>

        private val panItems = (0..<90).map {
            ItemStack(Items.DIAMOND)
        }

        private val handler = ItemStackHandler(100)
    }
}