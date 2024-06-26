package com.github.trcdevelopers.clayium.common.metatileentity

import com.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicCaInjector
import com.github.trcdevelopers.clayium.api.capability.impl.ResonanceManager
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.util.ResourceLocation

class CaInjectorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : WorkableMetaTileEntity(metaTileEntityId, tier, CRecipes.CA_INJECTOR) {
    val energyHolder = ClayEnergyHolder(this)
    val resonanceManager = ResonanceManager(this, 2)

    override val faceTexture = clayiumId("blocks/ca_injector")

    override val workable: AbstractRecipeLogic = RecipeLogicCaInjector(this, recipeRegistry, energyHolder, resonanceManager)

    override fun createMetaTileEntity(): MetaTileEntity {
        return CaInjectorMetaTileEntity(metaTileEntityId, tier)
    }
}