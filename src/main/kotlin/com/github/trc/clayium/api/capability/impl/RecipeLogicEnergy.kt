package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.recipe.IRecipeProvider
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World

open class RecipeLogicEnergy(
    metaTileEntity: MetaTileEntity,
    recipeRegistry: IRecipeProvider,
    private val energyHolder: ClayEnergyHolder,
) : AbstractRecipeLogic(metaTileEntity, recipeRegistry) {
    private var durationMultiplier = 1.0
    private var energyConsumingMultiplier = 1.0

    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        return energyHolder.drawEnergy(ClayEnergy((ce.energy * ocHandler.accelerationFactor).toLong()), simulate)
    }

    override fun applyOverclock(cePt: ClayEnergy, duration: Long, compensatedFactor: Double): LongArray {
        val (cet, duration) = super.applyOverclock(cePt, duration, compensatedFactor)
        return longArrayOf((cet * energyConsumingMultiplier).toLong(), (duration * durationMultiplier).toLong())
    }

    /**
     * Mainly used by Condensers, Grinders, Centrifuges.
     * The speed of these machines depends on the tier.
     */
    fun setDurationMultiplier(provider: (tier: Int) -> Double): RecipeLogicEnergy {
        durationMultiplier = provider(this.getTier())
        return this
    }

    /**
     * Mainly used by Condensers, Grinders, Centrifuges.
     * The speed of these machines depends on the tier.
     */
    fun setEnergyConsumingMultiplier(provider: (tier: Int) -> Double): RecipeLogicEnergy {
        energyConsumingMultiplier = provider(this.getTier())
        return this
    }

    override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
        super.addProbeInfo(mode, probeInfo, player, world, state, hitData)
        if (this.isWorking) {
            val cet = recipeCEt * ocHandler.accelerationFactor
            probeInfo.text("Using ${TextFormatting.RED}${cet.formatWithoutUnit()}${TextFormatting.WHITE} CE/t")
        }
    }
}