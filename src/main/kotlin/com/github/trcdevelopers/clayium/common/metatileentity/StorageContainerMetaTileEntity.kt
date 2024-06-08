package com.github.trcdevelopers.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.Widget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.metatileentity.AutoIoHandler
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.client.model.ModelTextures
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler

/**
 * todo
 */
class StorageContainerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, validOutputModesLists[1], "machine.${CValues.MOD_ID}.storage_container") {

    override val faceTexture = clayiumId("blocks/storage_container")
    override val requiredTextures = listOf(faceTexture, clayiumId("blocks/storage_container_side"), clayiumId("blocks/storage_container_top"))

    override val itemInventory: IItemHandlerModifiable = ItemStackHandler(1)
    override val importItems: IItemHandlerModifiable = itemInventory
    override val exportItems: IItemHandlerModifiable = itemInventory
    override val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    private val filterSlot = ItemStackHandler(1) // todo: maybe a specific impl?

    override fun createMetaTileEntity(): MetaTileEntity {
        return StorageContainerMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("storage_container"), "inventory"))
    }

    override fun buildUI(data: PosGuiData?, syncManager: GuiSyncManager?): ModularPanel? {
        val middleBar = Row()
            .widthRel(0.7f).height(26)
            .align(Alignment.Center)
            .top(30)
        middleBar.child(Widget()
                .size(26, 26).left(4)
                .background(ClayGuiTextures.LARGE_SLOT))
                .child(ItemSlot().left(8).top(4)
                    .slot(SyncHandlers.itemSlot(importItems, 0)
                        .singletonSlotGroup(2))
                    .background(IDrawable.EMPTY))
        middleBar.child(Widget()
                .size(26, 26).right(4)
                .background(ClayGuiTextures.LARGE_SLOT))
                .child(ItemSlot().right(8).top(4)
                    .slot(SyncHandlers.itemSlot(exportItems, 0)
                        .accessibility(false, true)
                        .singletonSlotGroup(0))
                    .background(IDrawable.EMPTY))
        return ModularPanel.defaultPanel("storage_container")
            .child(middleBar)
            .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(filterSlot, 0))
                .right(20).top(16))
            .child(playerInventoryTitle())
            .bindPlayerInventory()

    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(bakedTexGetter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        sideQuads = EnumFacing.HORIZONTALS.associateWith {
            ModelTextures.createQuad(it, bakedTexGetter.apply(clayiumId("blocks/storage_container_side")))
        }
        topQuad = ModelTextures.createQuad(EnumFacing.UP, bakedTexGetter.apply(clayiumId("blocks/storage_container_top")))
    }

    @SideOnly(Side.CLIENT)
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        if (state == null || side == null) return super.getQuads(state, side, rand)
        val quads = super.getQuads(state, side, rand)
        when {
            side.axis.isHorizontal -> sideQuads[side]?.let { quads.add(it) }
            side == EnumFacing.UP -> quads.add(topQuad)
        }
        return quads
    }

    companion object {
        @SideOnly(Side.CLIENT)
        private lateinit var sideQuads: Map<EnumFacing, BakedQuad>

        @SideOnly(Side.CLIENT)
        private lateinit var topQuad: BakedQuad
    }
}