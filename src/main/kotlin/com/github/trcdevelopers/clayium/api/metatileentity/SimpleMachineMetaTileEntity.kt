package com.github.trcdevelopers.clayium.api.metatileentity

import com.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

class SimpleMachineMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
    override val faceTexture: ResourceLocation,
    recipeRegistry: RecipeRegistry<*>,
    // saved for createMetaTileEntity()
    private val workableProvider: (MetaTileEntity, RecipeRegistry<*>, ClayEnergyHolder) -> AbstractRecipeLogic = ::RecipeLogicEnergy,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey, recipeRegistry) {

    constructor(
        metaTileEntityId: ResourceLocation,
        tier: ITier,
        recipeRegistry: RecipeRegistry<*>,
        workableProvider: (MetaTileEntity, RecipeRegistry<*>, ClayEnergyHolder) -> AbstractRecipeLogic = ::RecipeLogicEnergy,
    ) : this(
        metaTileEntityId, tier,
        validInputModesLists[recipeRegistry.maxInputs], validOutputModesLists[recipeRegistry.maxOutputs],
        translationKey = "machine.${metaTileEntityId.namespace}.${recipeRegistry.category.categoryName}",
        faceTexture = ResourceLocation(metaTileEntityId.namespace, "blocks/${recipeRegistry.category.categoryName}"),
        recipeRegistry, workableProvider
    )

    override val workable = workableProvider(this, recipeRegistry, clayEnergyHolder)

    override fun createMetaTileEntity(): MetaTileEntity {
        return SimpleMachineMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey, faceTexture, recipeRegistry, workableProvider)
    }

    override fun changeIoModesOnPlacement(placer: EntityLivingBase) {
        this._inputModes[EnumFacing.UP.index] = MachineIoMode.ALL
        this.refreshNeighborConnection(EnumFacing.UP)
        this._outputModes[EnumFacing.DOWN.index] = MachineIoMode.ALL
        this.refreshNeighborConnection(EnumFacing.DOWN)
        val placerFacing = placer.horizontalFacing
        this._inputModes[placerFacing.index] = MachineIoMode.CE
        this.refreshNeighborConnection(placerFacing.opposite)
        super.changeIoModesOnPlacement(placer)
    }
}