package com.github.trc.clayium.common.metatileentities

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.metatileentity.AbstractItemGeneratorMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MteRenderingConfig
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

class SaltExtractorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : AbstractItemGeneratorMetaTileEntity(
    metaTileEntityId, tier,
    validInputModes = energyAndNone, validOutputModes = validOutputModesLists[1],
    name = "salt_extractor") {

    override val progressPerItem: Int = 100
    override val progressPerTick = when (tier.numeric) {
        4 -> 50
        5 -> 200
        6 -> 1000
        7 -> 8000
        else -> 1
    }

    // wait for oreDict registration
    override val generatingItem by lazy { OreDictUnifier.get(OrePrefix.dust, CMaterials.salt) }

    private val clayEnergyHolder = ClayEnergyHolder(this)
    /*
    5(Efficiency)*300uCE=1500uCE(energy per tick)
    energy per item = 100/(water*5(efficiency))*1500uCE = 15mCE (when 2 water)
    energyPerProgress = 15mCE/100(progressMax)=150uCE
     */
    private val energyPerProgress = ClayEnergy.micro(150)

    override fun createMetaTileEntity(): MetaTileEntity {
        return SaltExtractorMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    override fun onPlacement() {
        setInput(this.frontFacing.opposite, MachineIoMode.CE)
        super.onPlacement()
    }

    override fun isTerrainValid(): Boolean {
        var waterCount = 0
        for (side in EnumFacing.entries) {
            val neighborMaterial = getNeighborBlockState(side)?.material
            if (neighborMaterial == net.minecraft.block.material.Material.WATER) {
                waterCount++
            }
            if (waterCount >= 2) return true
        }
        return false
    }

    override fun canProgress(): Boolean {
        return super.canProgress() && this.clayEnergyHolder.drawEnergy(energyPerProgress.times(progressPerTick), simulate = false)
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/salt_extractor"))
    }
}