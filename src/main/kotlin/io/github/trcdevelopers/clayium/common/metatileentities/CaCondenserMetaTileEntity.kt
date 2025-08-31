package io.github.trcdevelopers.clayium.common.metatileentities

import io.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicEnergy
import io.github.trcdevelopers.clayium.api.capability.impl.ResonanceManager
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.util.ResourceLocation
import kotlin.math.ln

class CaCondenserMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : WorkableMetaTileEntity(metaTileEntityId, tier, CRecipes.CA_CONDENSER) {
    val energyHolder = ClayEnergyHolder(this)
    val resonanceManager = ResonanceManager(this, 2)

    private val craftTimeMultiplier = when (tier.numeric) {
        10 -> 10
        11 -> 100
        else -> 1
    }

    override val workable: AbstractRecipeLogic = RecipeLogicCaCondenser()

    override fun createMetaTileEntity(): MetaTileEntity {
        return CaCondenserMetaTileEntity(metaTileEntityId, tier)
    }

    private inner class RecipeLogicCaCondenser : RecipeLogicEnergy(this@CaCondenserMetaTileEntity, recipeRegistry, energyHolder) {
        //todo 生成される量を加工開始時にも考慮するべきか否か
        override fun completeWork() {
            currentProgress = 0
            TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs.map { it.apply {
                count = (ln(resonanceManager.resonance).toInt() + 1).coerceIn(1..64)
            } }
            )
        }

        override fun getProgressPerTick(): Long {
            return craftTimeMultiplier.toLong()
        }
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/ca_condenser"))
    }
}