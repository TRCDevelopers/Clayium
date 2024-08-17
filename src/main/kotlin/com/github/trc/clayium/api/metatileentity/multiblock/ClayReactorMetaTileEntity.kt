package com.github.trc.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserAcceptor
import com.github.trc.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.laser.IClayLaser
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockLogic.StructureValidationResult
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.util.TransferUtils
import com.github.trc.clayium.common.util.UtilLocale
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

class ClayReactorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
): WorkableMetaTileEntity(
    metaTileEntityId, tier,
    validInputModesLists[2], validOutputModesLists[2],
    "machine.${CValues.MOD_ID}.clay_reactor",
    CRecipes.CLAY_REACTOR
), IClayLaserAcceptor {
    private val multiblockLogic = MultiblockLogic(this, ::checkStructure)

    var laser: IClayLaser? = null
        private set

    fun getFaceInvalid() = clayiumId("blocks/reactor")
    fun getFaceValid() = clayiumId("blocks/reactor_1")
    override val faceTexture get() = if (multiblockLogic.structureFormed) getFaceValid() else getFaceInvalid()
    override val requiredTextures get() = listOf(getFaceValid(), getFaceInvalid())

    private fun checkStructure(handler: MultiblockLogic): StructureValidationResult {
        val world = world ?: return StructureValidationResult.Invalid
        val controllerPos = pos ?: return StructureValidationResult.Invalid
        val mbParts = mutableListOf<IMultiblockPart>()
        val tiers = mutableListOf<ITier>()
        for (yy in -1..1) {
            for (xx in -1..1) {
                for (zz in 0..2) {
                    val relPos = handler.getControllerRelativeCoord(controllerPos, xx, yy, zz)
                    if (yy == 1 && xx == 0 && zz == 1) {
                        val laserProxy = getLaserProxy(relPos) ?: return StructureValidationResult.Invalid
                        mbParts.add(laserProxy)
                        tiers.add(laserProxy.tier)
                    }
                    val result = handler.isPosValidForMutliblock(world, relPos)
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

    private fun getLaserProxy(pos: BlockPos): LaserProxyMetaTileEntity? {
        return world.getMetaTileEntity(pos) as? LaserProxyMetaTileEntity
    }

    override val workable: MultiblockRecipeLogic = ClayReactorRecipeLogic(this)

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayReactorMetaTileEntity(metaTileEntityId, tier)
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        syncManager.syncValue("clayLaser", SyncHandlers.intNumber(
            { laser?.toInt() ?: -1 },
            { this.laser = if (it == -1) null else ClayLaser.fromInt(it, EnumFacing.UP) }
        ))
        return super.buildMainParentWidget(syncManager)
            .child(IKey.dynamic { I18n.format("gui.clayium.laser_energy", UtilLocale.laserNumeral(this.laser?.energy?.toLong() ?: 0L)) }.asWidget()
                .align(Alignment.BottomRight))
            .child(multiblockLogic.tierTextWidget(syncManager)
                .align(Alignment.BottomCenter))
    }

    override fun laserChanged(irradiatedSide: EnumFacing, laser: IClayLaser?) {
        this.laser = laser
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR) {
            return ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR.cast(this)
        }
        return super.getCapability(capability, facing)
    }

    private inner class ClayReactorRecipeLogic(private val clayReactor: ClayReactorMetaTileEntity)
        : MultiblockRecipeLogic(clayReactor, CRecipes.CLAY_REACTOR, multiblockLogic::structureFormed) {
        override fun updateWorkingProgress() {
            if (drawEnergy(recipeCEt)) {
                currentProgress++
                currentProgress += clayReactor.laser?.energy?.toLong() ?: 0L
            }
            if (currentProgress > requiredProgress) {
                currentProgress = 0
                TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs)
            }
        }
    }
}