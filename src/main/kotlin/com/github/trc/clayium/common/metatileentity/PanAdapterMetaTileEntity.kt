package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.drawable.GuiTextures
import com.cleanroommc.modularui.drawable.ItemDrawable
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.PageButton
import com.cleanroommc.modularui.widgets.PagedWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.pan.IPanAdapter
import com.github.trc.clayium.api.pan.IPanCable
import com.github.trc.clayium.api.pan.IPanEntry
import com.github.trc.clayium.api.pan.IPanNotifiable
import com.github.trc.clayium.api.pan.isPanCable
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.toList
import com.github.trc.clayium.client.model.ModelTextures
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import java.util.function.Function

class PanAdapterMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, onlyNoneList, onlyNoneList, "${CValues.MOD_ID}.machine.pan_adapter"), IPanAdapter {

    override val requiredTextures get() = listOf(clayiumId("blocks/pan_adapter"))

    override val importItems = EmptyItemStackHandler
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = EmptyItemStackHandler

    private val pageNum = when (tier.numeric) {
        10 -> 1
        11 -> 2
        12 -> 4
        13 -> 8
        else -> 1
    }

    val recipeInventories = List(pageNum) { ItemStackHandler(9) }
    private var network: IPanNotifiable? = null

    override fun createMetaTileEntity(): MetaTileEntity {
        return PanAdapterMetaTileEntity(metaTileEntityId, tier)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.PAN_CABLE -> capability.cast(IPanCable.INSTANCE)
            capability === ClayiumTileCapabilities.PAN_ADAPTER -> capability.cast(this)
            else -> super.getCapability(capability, facing)
        }
    }

    override fun getEntries(): Set<IPanEntry> {
        val world = world ?: return emptySet()
        val pos = pos ?: return emptySet()
        val entries = mutableSetOf<IPanEntry>()
        for (side in EnumFacing.entries) {
            val factory = ClayiumApi.PAN_ENTRY_FACTORIES.firstOrNull {
                it.matches(world, pos.offset(side))
            } ?: continue
            recipeInventories.forEach {
                val entry = factory.getEntry(it.toList())
                if (entry != null) entries.add(entry)
            }
        }
        return entries
    }

    override fun onNeighborChanged(facing: EnumFacing) {
        super.onNeighborChanged(facing)
        if (world!!.isPanCable(pos!!.offset(facing))) {
            network?.notifyNetwork()
        }
    }

    override fun onRemoval() {
        super.onRemoval()
        network?.notifyNetwork()
    }

    override fun setCore(network: IPanNotifiable) {
        this.network = network
    }

    override fun coreRemoved() {
        network = null
    }

    override fun buildUI(data: PosGuiData?, syncManager: GuiSyncManager?): ModularPanel? {
        val tabController = PagedWidget.Controller()
        return ModularPanel.defaultPanel("pan_adapter")
            .child(Row().coverChildren().topRel(0f, 4, 1f)
                .child(ParentWidget().coverChildren()
                    .child(PageButton(0, tabController)
                        .tab(GuiTextures.TAB_TOP, -1))
                    .child(ItemDrawable(ItemStack(Items.APPLE)).asWidget()
                        .size(16, 16)
                        .align(Alignment.Center))
                )
                .child(ParentWidget().coverChildren()
                    .child(PageButton(1, tabController)
                        .tab(GuiTextures.TAB_TOP, 0))
                    .child(ItemDrawable(ItemStack(Items.DIAMOND)).asWidget()
                        .size(16, 16)
                        .align(Alignment.Center))
                )
            )
            .child(PagedWidget()
                .controller(tabController)
                .sizeRel(1f)
                .addPage(ParentWidget().sizeRel(1f))
                .addPage(ParentWidget().sizeRel(1f))
            )
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        registerItemModelDefault(item, meta, "pan_adapter")
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val sprite = getter.apply(clayiumId("blocks/pan_adapter"))
        adapterQuads = EnumFacing.entries.map {
            ModelTextures.createQuad(it, sprite)
        }
    }

    override fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        if (state == null || side == null) return
        quads.add(adapterQuads[side.index])
    }

    companion object {
        private lateinit var adapterQuads: List<BakedQuad>
    }
}