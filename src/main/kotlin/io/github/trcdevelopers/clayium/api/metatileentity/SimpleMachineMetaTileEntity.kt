package io.github.trcdevelopers.clayium.api.metatileentity

import io.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicEnergy
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.util.ResourceLocation

class SimpleMachineMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    recipeRegistry: RecipeRegistry<*>,
    // saved for createMetaTileEntity()
    private val workableProvider: (MetaTileEntity, RecipeRegistry<*>, ClayEnergyHolder) -> AbstractRecipeLogic = ::RecipeLogicEnergy,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, recipeRegistry) {

    constructor(
        metaTileEntityId: ResourceLocation,
        tier: ITier,
        recipeRegistry: RecipeRegistry<*>,
        workableProvider: (MetaTileEntity, RecipeRegistry<*>, ClayEnergyHolder) -> AbstractRecipeLogic = ::RecipeLogicEnergy,
    ) : this(
        metaTileEntityId, tier,
        validInputModesLists[recipeRegistry.maxInputs], validOutputModesLists[recipeRegistry.maxOutputs],
        recipeRegistry,
        workableProvider,
    )

    override val workable = workableProvider(this, recipeRegistry, clayEnergyHolder)

    override fun createMetaTileEntity(): MetaTileEntity {
        return SimpleMachineMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, recipeRegistry, workableProvider)
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.builder().face(ResourceLocation(metaTileEntityId.namespace, "blocks/${recipeRegistry.category.categoryName}")).build()
    }
}