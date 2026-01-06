package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.recipe.RecipeProcessingJob
import io.github.trcdevelopers.clayium.api.sync.ClayiumSyncManager
import io.github.trcdevelopers.clayium.api.util.Mods
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import mcjty.theoneprobe.api.TextStyleClass
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.common.Optional

class SolarRecipeProcessor(
    syncManager: ClayiumSyncManager,
    metaTileEntity: MetaTileEntity,
) : RecipeProgressTracker(syncManager, metaTileEntity) {

    private var ceGeneratedPerTick = ClayEnergy.ZERO

    /**
     * public for sync.
     */
    var clayEnergy = ClayEnergy.ZERO

    constructor(metaTileEntity: MetaTileEntity) : this(metaTileEntity.clayiumSyncManager, metaTileEntity)

    override fun startProcessing(job: RecipeProcessingJob) {
        super.startProcessing(job)
        this.ceGeneratedPerTick = job.clayEnergyPerTick
    }

    override fun updateProgress() {
        super.updateProgress()
        this.clayEnergy += this.ceGeneratedPerTick
    }

    override fun reset() {
        super.reset()
        this.ceGeneratedPerTick = ClayEnergy.ZERO
        this.clayEnergy = ClayEnergy.ZERO
    }

    override fun canProgress(): Boolean {
        val world = metaTileEntity.world ?: return false
        val pos = metaTileEntity.pos ?: return false
        return world.canSeeSky(pos.up())
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = super.serializeNBT()
        data.setLong("clayEnergy", clayEnergy.energy)
        data.setLong("ceGeneratedPerTick", ceGeneratedPerTick.energy)
        return data
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        super.deserializeNBT(nbt)
        this.clayEnergy = ClayEnergy(nbt.getLong("clayEnergy"))
        this.ceGeneratedPerTick = ClayEnergy(nbt.getLong("ceGeneratedPerTick"))
    }

    @Optional.Method(modid = Mods.Names.THE_ONE_PROBE)
    override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
        probeInfo.text("${TextStyleClass.OK}${this.ceGeneratedPerTick.format()}${TextStyleClass.INFO}/t")
    }
}