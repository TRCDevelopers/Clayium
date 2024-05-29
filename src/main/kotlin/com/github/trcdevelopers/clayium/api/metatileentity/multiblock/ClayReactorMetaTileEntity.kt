package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.IClayLaserAcceptor
import com.github.trcdevelopers.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.BlockMachineHull
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
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

    override val workable: MultiblockRecipeLogic
        get() = TODO("Not yet implemented")

    override fun createMetaTileEntity(): MetaTileEntity {
        TODO("Not yet implemented")
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        TODO("Not yet implemented")
    }

    override fun laserChanged(irradiatedSide: EnumFacing, laser: IClayLaser?) {
        this.laser = laser
    }
}