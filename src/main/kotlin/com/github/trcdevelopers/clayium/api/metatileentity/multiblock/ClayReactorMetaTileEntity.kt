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
import com.github.trcdevelopers.clayium.api.capability.IClayLaserAcceptor
import com.github.trcdevelopers.clayium.api.capability.impl.ClayReactorRecipeLogic
import com.github.trcdevelopers.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.BlockMachineHull
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ClayReactorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
): MultiblockControllerBase(
    metaTileEntityId, tier,
    validInputModesLists[2], validOutputModesLists[2],
    "machine.${CValues.MOD_ID}.clay_blast_furnace",
    CRecipes.CLAY_REACTOR
), IClayLaserAcceptor {

    var laser: IClayLaser? = null
        private set

    override val faceWhenDeconstructed: ResourceLocation = clayiumId("blocks/reactor")
    override val faceWhenConstructed: ResourceLocation = clayiumId("blocks/reactor_1")
    override var faceTexture: ResourceLocation? = faceWhenDeconstructed
    override val allFaceTextures: List<ResourceLocation> = listOf(faceWhenDeconstructed, faceWhenConstructed)

    override fun isConstructed(): Boolean {
        val world = world ?: return false
        val controllerPos = pos ?: return false
        val mbParts = mutableListOf<IMultiblockPart>()
        val tiers = mutableListOf<ITier>()
        for (yy in -1..1) {
            for (xx in -1..1) {
                for (zz in 0..2) {
                    val mbPartPos = getControllerRelativeCoord(controllerPos, xx, yy, zz)
                    val (isValid, mbPart, tier) = isPosValidForMultiblock(world, mbPartPos)
                    if (!isValid) {
                        recipeLogicTier = 0
                        writeStructureValidity(false)
                        return false
                    }
                    mbPart?.let { mbParts.add(it) }
                    tier?.let { tiers.add(it) }
                }
            }
        }
        mbParts.forEach { it.addToMultiblock(this) }
        multiblockParts.addAll(mbParts)
        recipeLogicTier = calcTier(tiers.map { it.numeric })
        if (!structureFormed) {
            writeStructureValidity(true)
        }
        return true
    }

    private fun isPosValidForMultiblock(world: World, pos: BlockPos): Triple<Boolean, IMultiblockPart?, ITier?> {
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

    override val workable: MultiblockRecipeLogic = ClayReactorRecipeLogic(this)

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayReactorMetaTileEntity(metaTileEntityId, tier)
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.syncValue("mbTier", 3, SyncHandlers.intNumber({ recipeLogicTier }, { recipeLogicTier = it }))
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
                .child(IKey.dynamic { I18n.format("tooltip.clayium.tier", recipeLogicTier) }.asWidget()
                    .align(Alignment.BottomCenter))
        )

        return panel.bindPlayerInventory()
    }

    override fun laserChanged(irradiatedSide: EnumFacing, laser: IClayLaser?) {
        this.laser = laser
    }
}