package com.github.trcdevelopers.clayium.common.metatileentity

import com.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trcdevelopers.clayium.api.capability.impl.ResonanceManager
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.util.TransferUtils
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

    override val faceTexture = clayiumId("blocks/ca_condenser")

    override val workable: AbstractRecipeLogic = RecipeLogicCaCondenser()

    override fun createMetaTileEntity(): MetaTileEntity {
        return CaCondenserMetaTileEntity(metaTileEntityId, tier)
    }

    private inner class RecipeLogicCaCondenser : RecipeLogicEnergy(this@CaCondenserMetaTileEntity, recipeRegistry, energyHolder) {
        override fun completeRecipe() {
            currentProgress = 0
            TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs.map { it.apply {
                count = (ln(resonanceManager.resonance).toInt() + 1).coerceIn(1..64)
            } }
            )
        }

        override fun updateRecipeProgress() {
            if (drawEnergy(recipeCEt)) currentProgress += craftTimeMultiplier
            if (currentProgress > requiredProgress) {
                completeRecipe()
            }
        }
    }
}