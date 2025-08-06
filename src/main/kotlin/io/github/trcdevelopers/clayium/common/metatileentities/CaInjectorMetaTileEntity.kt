package io.github.trcdevelopers.clayium.common.metatileentities

import io.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicCaInjector
import io.github.trcdevelopers.clayium.api.capability.impl.ResonanceManager
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.util.ResourceLocation

class CaInjectorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : WorkableMetaTileEntity(metaTileEntityId, tier, CRecipes.CA_INJECTOR) {
    val resonanceManager = ResonanceManager(this, 2)
    override val workable = RecipeLogicCaInjector(this, recipeRegistry, clayEnergyHolder, resonanceManager)

    override fun createMetaTileEntity(): MetaTileEntity {
        return CaInjectorMetaTileEntity(metaTileEntityId, tier)
    }

    override val renderingConfig: MteRenderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/ca_injector"))
    }

    companion object {
        /**
         * Required Antimatter amounts:
         * 1->2: 1, 2->3: 2, 3->4: 2, 4->5: 3,
         * 5->6: 4, 6->7: 5, 7->8: 8, 8->9: 10,
         * 9->10: 13, 10->11: 17, 11->12: 23, 12->13: 30
         */
        val ANTIMATTER_AMOUNTS = intArrayOf(1, 2, 2, 3, 4, 5, 8, 10, 13, 17, 23, 30)

        const val DURATION = 4000
        const val CE_FACTOR = 3.0
    }
}