package com.github.trc.clayium.api.metatileentity

import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.util.ResourceLocation

class SimpleMachineMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    override val faceTexture: ResourceLocation,
    recipeRegistry: RecipeRegistry<*>,
    // saved for createMetaTileEntity()
    private val workableProvider:
        (MetaTileEntity, RecipeRegistry<*>, ClayEnergyHolder) -> AbstractRecipeLogic =
        ::RecipeLogicEnergy,
) :
    WorkableMetaTileEntity(
        metaTileEntityId,
        tier,
        validInputModes,
        validOutputModes,
        recipeRegistry
    ) {

    constructor(
        metaTileEntityId: ResourceLocation,
        tier: ITier,
        recipeRegistry: RecipeRegistry<*>,
        workableProvider:
            (MetaTileEntity, RecipeRegistry<*>, ClayEnergyHolder) -> AbstractRecipeLogic =
            ::RecipeLogicEnergy,
    ) : this(
        metaTileEntityId,
        tier,
        validInputModesLists[recipeRegistry.maxInputs],
        validOutputModesLists[recipeRegistry.maxOutputs],
        faceTexture =
            ResourceLocation(
                metaTileEntityId.namespace,
                "blocks/${recipeRegistry.category.categoryName}"
            ),
        recipeRegistry,
        workableProvider
    )

    override val workable = workableProvider(this, recipeRegistry, clayEnergyHolder)

    override fun createMetaTileEntity(): MetaTileEntity {
        return SimpleMachineMetaTileEntity(
            metaTileEntityId,
            tier,
            validInputModes,
            validOutputModes,
            faceTexture,
            recipeRegistry,
            workableProvider
        )
    }
}
