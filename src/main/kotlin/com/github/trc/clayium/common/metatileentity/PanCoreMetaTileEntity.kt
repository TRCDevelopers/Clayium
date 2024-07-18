package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Grid
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import com.github.trc.clayium.api.CValues
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
import com.github.trc.clayium.client.model.ModelTextures
import com.github.trc.clayium.common.config.ConfigCore
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
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import java.util.function.Function

class PanCoreMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, onlyNoneList, onlyNoneList, "${CValues.MOD_ID}.pan_core"), IPanNotifiable {
    override val importItems = EmptyItemStackHandler
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = EmptyItemStackHandler

    private val panEntries = mutableSetOf<IPanEntry>()
    private var networkNotified = false
    private val adapters = mutableListOf<IPanAdapter>()

    override fun update() {
        super.update()
        onServer {
            if (networkNotified && offsetTimer % REFRESH_RATE_TICKS == 0L) {
                refreshNetwork()
                networkNotified = false
            }
        }
    }

    private fun refreshNetwork() {
        this.panEntries.clear()
        val pos = this.pos ?: return
        val nodes = mutableSetOf<BlockPos>()
        searchNodes(nodes, pos, 0)
        for (pos in nodes) {
            val tileEntity = world?.getTileEntity(pos) ?: continue
            val adapter = tileEntity.getCapability(ClayiumTileCapabilities.PAN_ADAPTER, null)
                ?: continue
            adapters.add(adapter)
            adapter.setCore(this)
            this.panEntries.addAll(adapter.getEntries())
        }
    }

    private fun searchNodes(nodes: MutableSet<BlockPos>, pos: BlockPos, depth: Int) {
        if (depth > ConfigCore.misc.panMaxSearchDistance) return
        val world = this.world ?: return
        for (side in EnumFacing.entries) {
            val target = pos.offset(side)
            if (world.isPanCable(target) && target !in nodes) {
                nodes.add(target)
                searchNodes(nodes, target, depth + 1)
            }
        }
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return PanCoreMetaTileEntity(metaTileEntityId, tier)
    }

    override fun onRemoval() {
        super.onRemoval()
        for (adapter in adapters) {
            adapter.coreRemoved()
        }
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.PAN_CABLE) {
            return capability.cast(IPanCable.INSTANCE)
        }
        return super.getCapability(capability, facing)
    }

    override fun notifyNetwork() {
        networkNotified = true
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
        const val REFRESH_RATE_TICKS = 200
        private lateinit var panCoreQuads: MutableList<BakedQuad>

        private val panItems = (0..<90).map {
            ItemStack(Items.DIAMOND)
        }

        private val handler = ItemStackHandler(100)
    }
}