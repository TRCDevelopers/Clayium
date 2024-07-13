package com.github.trc.clayium.api.capability.impl

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.TextWidget
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.recipe.builder.ClayFabricatorRecipeBuilder
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry

open class ClayFabricatorRecipeLogic(
    metaTileEntity: MetaTileEntity,
    recipeRegistry: RecipeRegistry<ClayFabricatorRecipeBuilder>,
) : AbstractRecipeLogic(metaTileEntity, recipeRegistry) {

    protected var clayEnergy: ClayEnergy = ClayEnergy.ZERO

    override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
        if (simulate) return true
        clayEnergy += ce
        return true
    }

    override fun completeRecipe() {
        clayEnergy = ClayEnergy.ZERO
        super.completeRecipe()
    }

    fun createCeTextWidget(syncManager: GuiSyncManager): TextWidget {
        syncManager.syncValue("clayEnergy", SyncHandlers.longNumber(
            { clayEnergy.energy },
            { clayEnergy = ClayEnergy(it) }
        ))

        return IKey.dynamic { clayEnergy.format() }.asWidget()
    }
}