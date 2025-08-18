package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.screen.UISettings
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Flow
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_HEIGHT
import io.github.trcdevelopers.clayium.api.GUI_DEFAULT_WIDTH
import io.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import io.github.trcdevelopers.clayium.api.gui.data.MetaTileEntityGuiData
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.client.model.ModelTextures
import io.github.trcdevelopers.clayium.common.capability.impl.OnlyOutputFilteredItemHandlerModifiable
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.inventory.ItemHandlerWrappedInventoryCrafting
import io.github.trcdevelopers.clayium.common.util.DummyContainer
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.function.Function

class AutoCrafterMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    private val useEnergy: Boolean = true,
) : MetaTileEntity(
    metaTileEntityId, tier,
    if (useEnergy) validInputModesLists[1] else bufferValidInputModes,
    validOutputModesLists[1],
    "auto_crafter",
) {
    override val importItems = OnlyOutputFilteredItemHandlerModifiable(ClayiumItemStackHandler(this, 9), this::isItemValidForCraftingGrid)
    override val exportItems = ClayiumItemStackHandler(this, 6)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    private val sampleCraftingGrid = ClayiumItemStackHandler(this, 9)
    private val inventoryCrafting = ItemHandlerWrappedInventoryCrafting(importItems, DummyContainer)

    private val autoIoHandler = AutoIoHandler.Combined(this)

    private val requiredProgress = when (tier.numeric) {
        5 -> 20
        in 6..9 -> 1
        else -> 20
    }
    private var progress = 0
    private val craftAmountPerOperation = when (tier.numeric) {
        5, 6 -> 1
        7 -> 16
        8 -> 64
        9 -> 256
        else -> 1
    }
    private val cePerTick = when (tier.numeric) {
        5 -> ClayEnergy.ZERO
        6 -> ClayEnergy.micro(100)
        7 -> ClayEnergy.micro(400)
        8 -> ClayEnergy.micro(1600)
        9 -> ClayEnergy.micro(6400)
        else -> ClayEnergy.ZERO
    }

    private val clayEnergyHolder = if (useEnergy) ClayEnergyHolder(this) else null

    override fun onPlacement() {
        super.onPlacement()
        this.setInput(EnumFacing.UP, MachineIoMode.ALL)
        this.setOutput(EnumFacing.DOWN, MachineIoMode.ALL)
        this.setInput(this.frontFacing.opposite, MachineIoMode.CE)
    }

    override fun update() {
        super.update()
        if (isRemote) return
        this.distributeItems()

        if (this.progress < this.requiredProgress) { // consume energy only if progress is not enough
            if (this.useEnergy && !this.clayEnergyHolder!!.drawEnergy(this.cePerTick, false)) {
                return
            }
            this.progress += 1
        }
        if (this.progress >= this.requiredProgress) {
            val succeeded = this.craft()
            if (succeeded) {
                // Reset only if actually crafted something
                // If not crafted it will not reset, so energy consumption will stop
                this.progress = 0
            }
            repeat(this.craftAmountPerOperation - 1) { // already crafted once
                if (this.craft()) {
                    this.autoIoHandler.reTransferWithinTick()
                    this.distributeItems()
                } else {
                    return
                }
            }
        }
    }

    /**
     * Repeat for all grid slots:
     * 1. Get an item stack (stack A) from the slot.
     * 2. For the all other slots:
     *   1. Stack A matches with sample crafting grid item?
     *   2. If yes, then is the slot's stack empty?
     *   3. If yes, then the count of Stack A is greater than 1?
     *   4. If yes, then extract 1 from Stack A and put it into the slot.
     */
    private fun distributeItems() {
        for (i in 0..<9) {
            val stack = importItems.getStackInSlot(i).copy()
            if (stack.isEmpty) continue
            for (j in 0..<9) {
                if (i == j) continue
                if (isItemValidForCraftingGrid(j, stack) && importItems.getStackInSlot(j).isEmpty && stack.count > 1) {
                    stack.shrink(1)
                    val insert = stack.copyWithSize(1)
                    importItems.setStackInSlot(j, insert)
                    if (stack.count <= 1) break
                }
            }
            importItems.setStackInSlot(i, stack)
        }
    }

    private fun craft(): Boolean {
        for (i in 0..<9) {
            val sampleStack = sampleCraftingGrid.getStackInSlot(i)
            if (!sampleStack.isEmpty) {
                val stack = importItems.getStackInSlot(i)
                if (!isItemValidForCraftingGrid(i, stack)) return false
            }
        }
        val world = this.world ?: return false
        val recipe = CraftingManager.findMatchingRecipe(this.inventoryCrafting, world)
            ?: return false
        val result = recipe.getCraftingResult(this.inventoryCrafting)
            ?: return false
        val craftResults = CraftingManager.getRemainingItems(this.inventoryCrafting, world)
            .toMutableList() // add isn't supported by the original list
            .also { it.add(result) /* Main Craft Result */ }
        if (!TransferUtils.insertToHandler(exportItems, craftResults, true)) {
            return false // Not enough space in export items
        }
        // consume items from importItems
        for (i in 0..<9) {
            importItems.extractItem(i, 1, false)
        }
        // insert the craft results to exportItems
        TransferUtils.insertToHandler(exportItems, craftResults, false)
        return true
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.CLAY_ENERGY_HOLDER) {
            return capability.cast(this.clayEnergyHolder)
        }
        return super.getCapability(capability, facing)
    }

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

    /**
     * Return false for empty.
     */
    private fun isItemValidForCraftingGrid(index: Int, itemStack: ItemStack): Boolean {
        if (itemStack.isEmpty) return false
        val sampleItem = sampleCraftingGrid.getStackInSlot(index)
        val filterCapability = sampleItem.getCapability(ClayiumCapabilities.ITEM_FILTER, null)
        return filterCapability?.test(itemStack)
            ?: (sampleItem.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(sampleItem, itemStack))
    }

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: PanelSyncManager, settings: UISettings): ModularPanel {
        return ModularPanel.defaultPanel(translationKey, GUI_DEFAULT_WIDTH, GUI_DEFAULT_HEIGHT + 20)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        syncManager.registerSlotGroup("input_inventory", 3)
        return super.buildMainParentWidget(syncManager)
            .child(Flow.row().height(18 * 3).widthRel(1f).marginTop(13)
                .child(SlotGroupWidget.builder()
                    .matrix("SSS", "SSS", "SSS")
                    .key('S') {
                        MuiSlots.phantomSlot(sampleCraftingGrid, it)
                            .background(ClayGuiTextures.FILTER_SLOT)
                    }.build()
                    .marginLeft(0)
                )
                .child(SlotGroupWidget.builder()
                    .matrix("III", "III", "III")
                    .key('I') { i ->
                        MuiSlots.itemSlotBuilder(importItems, i)
                            .slotGroup("input_inventory")
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
            // @see clayEnergyHoler declaration, it's not null if useEnergy is true
            // we must use supplier to avoid NPE
            // TODO: Low priority. Create empty IClayEnergyHolder for NPE safety?
            .childIf(this.useEnergy) {
                clayEnergyHolder!!.createCeTextWidget(syncManager).bottom(12).left(0)
            }
            .childIf(this.useEnergy) {
                clayEnergyHolder!!.createSlotWidget().align(Alignment.BottomRight)
            }
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