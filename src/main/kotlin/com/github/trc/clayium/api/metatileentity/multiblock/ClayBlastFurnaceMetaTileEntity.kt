package com.github.trc.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockLogic.StructureValidationResult
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.util.ResourceLocation
import kotlin.let

class ClayBlastFurnaceMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) :
    WorkableMetaTileEntity(
        metaTileEntityId,
        tier,
        CRecipes.CLAY_BLAST_FURNACE,
    ) {
    private val multiblockLogic = MultiblockLogic(this, ::checkStructure)

    // todo: fix these code duplication?
    fun getFaceInvalid(): ResourceLocation = clayiumId("blocks/blastfurnace")

    fun getFaceValid() = clayiumId("blocks/blastfurnace_1")

    override val faceTexture
        get() = if (multiblockLogic.structureFormed) getFaceValid() else getFaceInvalid()

    override val requiredTextures
        get() = listOf(getFaceValid(), getFaceInvalid())

    override val importItems = NotifiableItemStackHandler(this, 2, this, isExport = false)
    override val exportItems = NotifiableItemStackHandler(this, 2, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)

    override val workable: MultiblockRecipeLogic =
        MultiblockRecipeLogic(this, recipeRegistry, multiblockLogic)

    private fun checkStructure(handler: MultiblockLogic): StructureValidationResult {
        val world = world
        val controllerPos = pos
        if (world == null || controllerPos == null) return StructureValidationResult.Invalid
        val mbParts = mutableListOf<IMultiblockPart>()
        val tiers = mutableListOf<ITier>()
        for (yy in 0..1) {
            for (xx in -1..1) {
                for (zz in 0..2) {
                    val mbPartPos = handler.getControllerRelativeCoord(controllerPos, xx, yy, zz)
                    val result = handler.isPosValidForMutliblock(world, mbPartPos)
                    when (result) {
                        MultiblockLogic.BlockValidationResult.Invalid ->
                            return StructureValidationResult.Invalid
                        is MultiblockLogic.BlockValidationResult.Matched -> {
                            result.tier?.let { tiers.add(it) }
                        }
                        is MultiblockLogic.BlockValidationResult.MultiblockPart ->
                            mbParts.add(result.part)
                    }
                }
            }
        }
        return StructureValidationResult.Valid(mbParts, tiers)
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(multiblockLogic.tierTextWidget(syncManager).align(Alignment.BottomCenter))
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayBlastFurnaceMetaTileEntity(metaTileEntityId, tier)
    }
}
