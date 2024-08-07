package com.github.trc.clayium.common.metatileentity.multiblock

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.IMultiblockPart
import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockTrait
import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockTrait.StructureValidationResult
import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockTrait.StructureValidationResult.Invalid
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.common.blocks.BlockCaReactorCoil
import com.github.trc.clayium.common.blocks.BlockCaReactorHull
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class CaReactorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1],
    "machines.${CValues.MOD_ID}.ca_reactor") {

    private val multiblockValidation = MultiblockTrait(this, ::checkStructure)

    fun getFaceInvalid(): ResourceLocation = clayiumId("blocks/ca_reactor_core_invalid")
    fun getFaceValid() = clayiumId("blocks/ca_reactor_core_valid")
    override val faceTexture get() = if (multiblockValidation.structureFormed) getFaceValid() else getFaceInvalid()
    override val requiredTextures get() = listOf(getFaceValid(), getFaceInvalid())

    override val importItems = NotifiableItemStackHandler(this, 1, this, isExport = false)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("ca_reactor")
    }

    private fun checkStructure(handler: MultiblockTrait): StructureValidationResult {
        var hullRankSum = 0
        var hullCount = 0

        val pos = pos ?: return Invalid
        val world = world ?: return Invalid

        val firstCoilPos = EnumFacing.entries.firstNotNullOfOrNull {
            if (world.getBlockState(pos.offset(it)).block is BlockCaReactorCoil) pos.offset(it) else null
        }
        if (firstCoilPos == null) return Invalid
        val parts = mutableListOf<IMultiblockPart>()
        val walked = mutableSetOf<BlockPos>()
        val isValid = searchAndValidateAdjacentCoil(firstCoilPos, walked)
        if (!isValid) return Invalid

        // Extra checks
        // Coil is surrounded by coils or hulls or MBPart and count the hulls
        // Interfaces and coil blocks of a lower tier than the core block cannot be used.
        for (pos in walked) {
            for (side in EnumFacing.entries) {
                val pos = pos.offset(side)
                val metaTileEntity = world.getMetaTileEntity(pos)
                val block = world.getBlockState(pos).block
                when {
                    metaTileEntity is IMultiblockPart -> {
                        if (metaTileEntity.tier.numeric < this.tier.numeric) return Invalid
                        parts.add(metaTileEntity)
                    }
                    metaTileEntity === this -> {}
                    block is BlockCaReactorHull -> {
                        if (block.getTier(world, pos).numeric < this.tier.numeric) return Invalid
                        hullCount++
                        hullRankSum += block.getCaRank(world, pos)
                    }
                    block is BlockCaReactorCoil -> {
                        if (block.getTier(world, pos).numeric < this.tier.numeric) return Invalid
                    }
                    else -> return Invalid
                }
            }
        }

        if (hullCount < REQUIRED_HULLS) return Invalid

        return StructureValidationResult.Valid(parts, emptyList())
    }

    private fun searchAndValidateAdjacentCoil(coilPos: BlockPos, walked: MutableSet<BlockPos>): Boolean {
        if (walked.contains(coilPos)) return true
        if (walked.size > MAX_COILS) return false
        walked.add(coilPos)
        val world = world ?: return false
        val coilPoses = mutableListOf<BlockPos>()
        var valid = true
        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    if (dx == 0 && dy == 0 && dz == 0) continue
                    val pos = coilPos.add(dx, dy, dz)
                    val block = world.getBlockState(pos).block
                    if (block is BlockCaReactorCoil) {
                        coilPoses.add(pos)
                    }
                }
            }
        }
        if (coilPoses.size == 2) {
            for (coilPos in coilPoses) {
                valid = valid && searchAndValidateAdjacentCoil(coilPos, walked)
            }
        } else {
            valid = false
        }
        return valid
    }

    override fun createMetaTileEntity() = CaReactorMetaTileEntity(metaTileEntityId, tier)

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        registerItemModelDefault(item, meta, "ca_reactor")
    }

    companion object {
        const val MAX_COILS = 128
        const val REQUIRED_HULLS = 50
    }
}