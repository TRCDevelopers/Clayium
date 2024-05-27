package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_STRUCTURE_VALIDITY
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trcdevelopers.clayium.api.metatileentity.AutoIoHandler
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.api.util.RelativeDirection
import com.github.trcdevelopers.clayium.common.blocks.BlockMachineHull
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.item.Item
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.model.ModelLoader
import kotlin.apply
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.let

class ClayBlastFurnaceMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MultiblockControllerBase(
    metaTileEntityId, tier,
    validInputModesLists[CRecipes.CLAY_BLAST_FURNACE.maxInputs], validOutputModesLists[CRecipes.CLAY_BLAST_FURNACE.maxOutputs],
    "machine.${CValues.MOD_ID}.clay_blast_furnace",
    CRecipes.CLAY_BLAST_FURNACE,
) {

    private val faceWhenDeconstructed = clayiumId("blocks/blastfurnace")
    private val faceWhenConstructed = clayiumId("blocks/blastfurnace_1")
    override val allFaceTextures = listOf(faceWhenDeconstructed, faceWhenConstructed)
    override var faceTexture = faceWhenDeconstructed
        private set

    override val importItems = NotifiableItemStackHandler(this, 2, this, isExport = false)
    override val exportItems = NotifiableItemStackHandler(this, 2, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    override val autoIoHandler = AutoIoHandler.Combined(this)

    override val workable: MultiblockRecipeLogic = MultiblockRecipeLogic(this, recipeRegistry)

    /**
     * only used for recipeLogics, so int is fine
     */
    private var multiblockTier: Int = 0

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayBlastFurnaceMetaTileEntity(metaTileEntityId, tier)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
            ModelResourceLocation(clayiumId("clay_blast_furnace"), "tier=${tier.numeric}")
        )
    }

    override fun isConstructed(): Boolean {
        val world = world ?: return false
        val controllerPos = pos ?: return false
        val mbParts = mutableListOf<IMultiblockPart>()
        val tiers = mutableListOf<ITier>()
        for (yy in 0..1) {
            for (xx in -1..1) {
                for (zz in 0..2) {
                    val mbPartPos = getControllerRelativeCoord(controllerPos, xx, yy, zz)
                    val (isValid, mbPart, tier) = isPosValidForMultiblock(world, mbPartPos)
                    if (!isValid) {
                        multiblockTier = 0
                        writeCustomData(UPDATE_STRUCTURE_VALIDITY) { writeBoolean(false) }
                        return false
                    }
                    mbPart?.let { mbParts.add(it) }
                    tier?.let { tiers.add(it) }
                }
            }
        }
        mbParts.forEach { it.addToMultiblock(this) }
        multiblockParts.addAll(mbParts)
        multiblockTier = calcTier(tiers.map { it.numeric })
        if (!structureFormed) {
            writeCustomData(UPDATE_STRUCTURE_VALIDITY) { writeBoolean(true) }
        }
        return true
    }

    //todo: tier iranai
    private fun isPosValidForMultiblock(world: IBlockAccess, pos: BlockPos): Triple<Boolean, IMultiblockPart?, ITier?> {
        if (CUtils.getMetaTileEntity(world, pos) == this) return Triple(true, null, null)

        CUtils.getMetaTileEntity(world, pos)?.let { metaTileEntity ->
            if (metaTileEntity is IMultiblockPart
                // already formed -> part is attached to this
                && (structureFormed || (!metaTileEntity.isAttachedToMultiblock() || metaTileEntity.canPartShare()))) {
                multiblockParts.add(metaTileEntity)
                return Triple(true, metaTileEntity, metaTileEntity.tier)
            }
        }

        val block = world.getBlockState(pos).block as? BlockMachineHull ?: return Triple(false, null, null)
        return Triple(true, null, block.getTier(world, pos))
    }

    // y
    // ^  z
    // | /
    // |/
    // - - - > x
    private fun getControllerRelativeCoord(pos: BlockPos, right: Int, up: Int, backwards: Int): BlockPos {
        val frontFacing = this.frontFacing
        val relRight = RelativeDirection.RIGHT.getActualFacing(frontFacing)
        val relUp = RelativeDirection.UP.getActualFacing(frontFacing)
        val relBackwards = RelativeDirection.BACK.getActualFacing(frontFacing)
        return BlockPos(
            pos.x + relRight.xOffset * right + relUp.xOffset * up + relBackwards.xOffset * backwards,
            pos.y + relRight.yOffset * right + relUp.yOffset * up + relBackwards.yOffset * backwards,
            pos.z + relRight.zOffset * right + relUp.zOffset * up + relBackwards.zOffset * backwards,
        )
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_STRUCTURE_VALIDITY -> {
                val structureFormed = buf.readBoolean()
                faceTexture = if (structureFormed) faceWhenConstructed else faceWhenDeconstructed
                this.structureFormed = structureFormed
                scheduleRenderUpdate()
            }
        }
        super.receiveCustomData(discriminator, buf)
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.syncValue("mbTier", 3, SyncHandlers.intNumber({ multiblockTier }, { multiblockTier = it }))
        val panel = ModularPanel.defaultPanel(this.metaTileEntityId.toString())

        // title
        panel.child(IKey.lang("machine.clayium.clay_blast_furnace").asWidget()
            .top(6)
            .left(6))

        val slotsAndProgressBar = Row()
            .widthRel(0.7f).height(26)
            .align(Alignment.Center)
            .top(30)
            .child(workable.getProgressBar(syncManager))

        syncManager.registerSlotGroup("input_inv", 1)
        slotsAndProgressBar.child(
            SlotGroupWidget.builder()
                .matrix("II")
                .key('I') { index ->
                    ItemSlot().slot(
                        SyncHandlers.itemSlot(importItems, index)
                            .slotGroup("input_inv"))
                        .apply {
                            if (index == 0)
                                background(ClayGuiTextures.IMPORT_1_SLOT)
                            else
                                background(ClayGuiTextures.IMPORT_2_SLOT)
                        }}
                .build()
                .align(Alignment.CenterLeft)
        )

        syncManager.registerSlotGroup("output_inv", 1)
        slotsAndProgressBar.child(
            SlotGroupWidget.builder()
                .matrix("II")
                .key('I') { index ->
                    ItemSlot().slot(
                        SyncHandlers.itemSlot(exportItems, index)
                            .accessibility(false, true)
                            .slotGroup("output_inv"))
                        .apply {
                            if (index == 0)
                                background(ClayGuiTextures.EXPORT_1_SLOT)
                            else
                                background(ClayGuiTextures.EXPORT_2_SLOT)
                        }}
                .build()
                .align(Alignment.CenterRight)
        )
        panel.child(
            Column().sizeRel(1f, 0.45f).top(0)
                .child(slotsAndProgressBar)
                .child(clayEnergyHolder.createSlotWidget()
                    .right(7).top(58)
                    .setEnabledIf { GuiScreen.isShiftKeyDown() }
                    .background(IDrawable.EMPTY))
                .child(clayEnergyHolder.createCeTextWidget(syncManager, 2)
                    .widthRel(0.5f)
                    .pos(6, 60))
                .child(IKey.dynamic { I18n.format("tooltip.clayium.tier", multiblockTier) }.asWidget()
                    .align(Alignment.BottomCenter))
        )

        return panel.bindPlayerInventory()
    }
}