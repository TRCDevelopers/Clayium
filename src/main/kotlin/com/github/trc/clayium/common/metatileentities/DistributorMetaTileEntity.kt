package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.GUI_DEFAULT_HEIGHT
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.*
import com.github.trc.clayium.client.model.ModelTextures
import com.github.trc.clayium.common.util.CNbtUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import org.jetbrains.annotations.VisibleForTesting
import java.util.EnumMap
import java.util.function.Function
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.math.min

class DistributorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, validOutputModesLists[1], "distributor") {

    private val groupX = when (tier.numeric) {
        7, 8, 9 -> tier.numeric - 5
        else -> 1
    }
    private val groupY = when (tier.numeric) {
        7, 8 -> 2
        9 -> 3
        else -> 1
    }

    private val groups = List(groupX * groupY) { ClayiumItemStackHandler(this, 4) }

    override val itemInventory = CombinedInvWrapper(*groups.toTypedArray())
    override val importItems = itemInventory
    override val exportItems = itemInventory

    private var groupIndex = 0

    @Suppress("unused")
    @VisibleForTesting
    val ioHandler = DistributorIoHandler()

    override fun onPlacement() {
        for (side in EnumFacing.entries) {
            if (side == this.frontFacing.opposite) {
                this.setInput(side, MachineIoMode.ALL)
            } else {
                this.setOutput(side, MachineIoMode.ALL)
            }
        }
        super.onPlacement()
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        val height = GUI_DEFAULT_HEIGHT - 50 + (18*2 * groupY + 2 * (groupY - 1))
        return ModularPanel.defaultPanel(translationKey, GUI_DEFAULT_WIDTH, height)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        val groups = groups.mapIndexed { i, handler ->
            syncManager.registerSlotGroup("group$i", 2)
            val group = SlotGroupWidget.builder()
                .matrix("II", "II")
                .key('I') { j ->
                    ItemSlot().slot(SyncHandlers.itemSlot(handler, j).slotGroup("group$i"))
                }
                .build()
            group
        }
        val slotGroupRows = groups.windowed(this.groupX, this.groupX).map { slotGroupList ->
            val row = Row()
                .size((18 * 2) * this.groupX + 2 * (this.groupX - 1), 18 * 2)
            slotGroupList.forEachIndexed { i, g ->
                row.child(g.marginLeft(2 * min(i, 1)))
            }
            row
        }
        val column = Column()
            .width(18 * 2 * this.groupX + 2 * (this.groupX - 1))
            .height(18 * 2 * this.groupY + 2 * (this.groupY - 1))
        val w = super.buildMainParentWidget(syncManager)
        slotGroupRows.forEachIndexed { i, row ->
            column.child(row.marginTop(2 * min(i, 1)))
        }
        return w.child(column.align(Alignment.Center))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            // don't expose inventory
            return null
        }
        return super.getCapability(capability, facing)
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val sprite = getter.apply(clayiumId("blocks/distributor"))
        distributorQuads = EnumFacing.entries.map { ModelTextures.createQuad(it, sprite) }
    }

    @SideOnly(Side.CLIENT)
    override fun getQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        super.getQuads(quads, state, side, rand)
        if (state == null || side == null || state !is IExtendedBlockState) return
        quads.add(distributorQuads[side.index])
    }

    override fun onReplace(world: World, pos: BlockPos, newMetaTileEntity: MetaTileEntity, oldMteData: NBTTagCompound) {
        CNbtUtils.handleInvSizeDifference(world, pos, oldMteData, IMPORT_INVENTORY, newMetaTileEntity.itemInventory)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return DistributorMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    /**
     * imported or exported -> pointer++
     * if the exportation is one lap behind, stop importing
     */
    inner class DistributorIoHandler : AutoIoHandler.Combined(this@DistributorMetaTileEntity, isBuffer = true) {
        private var oneLapBehind = false
        private var importPtr = 0
            set(value) {
                field = value
                if (field >= groups.size) field = 0
                this.oneLapBehind = (field == exportPtr)
            }
        private var exportPtr = 0
            set(value) {
                field = value
                if (field >= groups.size) field = 0
                this.oneLapBehind = false
            }
        private var lastDirection = EnumFacing.DOWN

        override fun serializeNBT(): NBTTagCompound {
            return super.serializeNBT().apply {
                setInteger("importPtr", importPtr)
                setInteger("exportPtr", exportPtr)
                setBoolean("oneLapBehind", oneLapBehind)
                setInteger("lastDirection", lastDirection.index)
            }
        }

        override fun deserializeNBT(data: NBTTagCompound) {
            super.deserializeNBT(data)
            importPtr = data.getInteger("importPtr")
            exportPtr = data.getInteger("exportPtr")
            oneLapBehind = data.getBoolean("oneLapBehind")
            lastDirection = EnumFacing.byIndex(data.getInteger("lastDirection"))
        }

        override fun importFromNeighbors() {
            if (oneLapBehind) return
            var remainingImport = amountPerAction
            val importItems = groups[importPtr]
            for (side in EnumFacing.entries) {
                if (!(remainingImport > 0 && isImporting(side))) continue
                remainingImport = transferItemStack(
                    from = metaTileEntity.getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite) ?: continue,
                    to = importItems,
                    amount = remainingImport,
                )
            }
            if (remainingImport != amountPerAction) importPtr++
        }

        override fun exportToNeighbors() {
            val neighborMap = EnumFacing.entries.enumMapNotNull { side ->
                if (!isExporting(side)) return@enumMapNotNull null
                getNeighbor(side)?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite)
            }
            @Suppress("UsePropertyAccessSyntax") //synthetic properties
            if (neighborMap.isEmpty()) return
            val currentInv = groups[exportPtr]

            if (distribute(currentInv, neighborMap)) exportPtr++
        }

        /**
         * @return true if insertion was proceeded, false if no insertion was proceeded
         */
        @VisibleForTesting
        fun distribute(source: IItemHandler, neighborMap: Map<EnumFacing, IItemHandler>): Boolean {
            var remainingExport = amountPerAction
            for (exportSlot in 0..<source.slots) {
                // create a copy, so we can safely remove elements from this copied map
                // if a neighbor inventory is full, we remove it from the map
                val neighbors = EnumMap(neighborMap)
                val exported = source.extractItem(exportSlot, amountPerAction, true)
                val countPerNeighbor = exported.count / neighbors.size
                if (exported.isEmpty) continue

                var notInserted = 0
                for ((side, neighbor) in neighbors) {
                    // try bulk insert first
                    val toInsert = exported.copyWithSize(countPerNeighbor)
                    if (countPerNeighbor != 0) {
                        val remain = ItemHandlerHelper.insertItem(neighbor, toInsert, false)
                        val inserted = toInsert.count - remain.count
                        source.extractItem(exportSlot, inserted, false)
                        remainingExport -= inserted
                        notInserted += remain.count
                        if (!remain.isEmpty) {
                            neighbors.remove(side)
                        }
                    }
                }

                @Suppress("UsePropertyAccessSyntax") //synthetic properties
                if (neighbors.isEmpty()) continue

                // one by one insertion
                val nextNeighbor = generateSequence(lastDirection.next()) { current ->
                    @Suppress("UsePropertyAccessSyntax") //synthetic properties
                    if (neighbors.isEmpty()) {
                        return@generateSequence null
                    }
                    else {
                        for (i in 0..<6) {
                            val next = current.next()
                            if (neighbors.containsKey(next)) return@generateSequence next
                        }
                        return@generateSequence null
                    }
                }.iterator()

                notInserted += exported.count % neighbors.size
                val toInsertCount1 = exported.copyWithSize(1)
                while (remainingExport > 0 && notInserted > 0 && nextNeighbor.hasNext()) {
                    val side = nextNeighbor.next()
                    lastDirection = side
                    val handler = neighbors[side] ?: continue
                    val remain = ItemHandlerHelper.insertItem(handler, toInsertCount1, false)
                    if (!remain.isEmpty) {
                        neighbors.remove(side)
                        continue
                    }
                    source.extractItem(exportSlot, 1, false)
                    notInserted--
                    remainingExport--
                }
            }
            return remainingExport != amountPerAction
        }
    }

    companion object {
        private lateinit var distributorQuads: List<BakedQuad>
    }
}