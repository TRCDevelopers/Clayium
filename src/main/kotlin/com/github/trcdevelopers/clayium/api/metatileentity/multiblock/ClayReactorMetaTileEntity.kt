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
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.IClayLaserAcceptor
import com.github.trcdevelopers.clayium.api.capability.impl.ClayReactorRecipeLogic
import com.github.trcdevelopers.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trcdevelopers.clayium.api.laser.ClayLaser
import com.github.trcdevelopers.clayium.api.laser.IClayLaser
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.BlockMachineHull
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

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
                    if (yy == 1 && xx == 0 && zz == 1) {
                        val laserProxy = getLaserProxy(getControllerRelativeCoord(controllerPos, xx, yy, zz)) ?: return false
                        mbParts.add(laserProxy)
                        tiers.add(laserProxy.tier)
                    }
                    val mbPartPos = getControllerRelativeCoord(controllerPos, xx, yy, zz)
                    val (isValid, mbPart, tier) = isPosValidForMultiblock(world, mbPartPos)
                    if (!isValid) return false
                    mbPart?.let { mbParts.add(it) }
                    tier?.let { tiers.add(it) }
                }
            }
        }
        mbParts.forEach { it.addToMultiblock(this) }
        multiblockParts.addAll(mbParts)
        recipeLogicTier = calcTier(tiers.map { it.numeric })
        return true
    }

    private fun getLaserProxy(pos: BlockPos): LaserProxyMetaTileEntity? {
        val metaTileEntity = CUtils.getMetaTileEntity(world, pos)
        return if (metaTileEntity is LaserProxyMetaTileEntity) metaTileEntity else null
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
        syncManager.syncValue("laser", 4, SyncHandlers.intNumber(
            {
                val laser = this.laser ?: return@intNumber 0
                return@intNumber (laser.laserRed shl 16) or (laser.laserGreen shl 8) or laser.laserBlue
            },
            {
                if (it == 0) {
                    this.laser = null
                    return@intNumber
                }
                val laserRed = (it shr 16) and 0xFF
                val laserGreen = (it shr 8) and 0xFF
                val laserBlue = it and 0xFF
                this.laser = ClayLaser(EnumFacing.NORTH, laserRed, laserGreen, laserBlue)
            }
        ))
        val panel = super.buildUI(data, syncManager)
        panel.child(
            Column().sizeRel(0.6f, 0.1f).topRel(0.4f).right(6)
                .child(IKey.dynamic { I18n.format("tooltip.clayium.tier", recipeLogicTier) }.asWidget()
                    .align(Alignment.BottomLeft))
                .child(IKey.dynamic { I18n.format("gui.clayium.laser_energy", UtilLocale.laserNumeral(this.laser?.laserEnergy?.toLong() ?: 0L)) }.asWidget()
                    .align(Alignment.BottomRight))
        )
        return panel
    }

    override fun laserChanged(irradiatedSide: EnumFacing, laser: IClayLaser?) {
        this.laser = laser
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR) {
            return ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR.cast(this)
        }
        return super.getCapability(capability, facing)
    }
}