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

    override val faceWhenDeconstructed = clayiumId("blocks/blastfurnace")
    override val faceWhenConstructed = clayiumId("blocks/blastfurnace_1")
    override val allFaceTextures = listOf(faceWhenDeconstructed, faceWhenConstructed)
    override var faceTexture: ResourceLocation? = faceWhenDeconstructed

    override val importItems = NotifiableItemStackHandler(this, 2, this, isExport = false)
    override val exportItems = NotifiableItemStackHandler(this, 2, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    override val autoIoHandler = AutoIoHandler.Combined(this)

    override val workable: MultiblockRecipeLogic = MultiblockRecipeLogic(this, recipeRegistry)

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

    private fun isPosValidForMultiblock(world: IBlockAccess, pos: BlockPos): Triple<Boolean, IMultiblockPart?, ITier?> {
        if (CUtils.getMetaTileEntity(world, pos) == this) return Triple(true, null, null)

        CUtils.getMetaTileEntity(world, pos)?.let { metaTileEntity ->
            if (metaTileEntity is IMultiblockPart
                // already formed -> part is attached to this
                && (structureFormed || (!metaTileEntity.isAttachedToMultiblock || metaTileEntity.canPartShare()))) {
                multiblockParts.add(metaTileEntity)
                return Triple(true, metaTileEntity, metaTileEntity.tier)
            }
        }

        val block = world.getBlockState(pos).block as? BlockMachineHull ?: return Triple(false, null, null)
        return Triple(true, null, block.getTier(world, pos))
    }
}