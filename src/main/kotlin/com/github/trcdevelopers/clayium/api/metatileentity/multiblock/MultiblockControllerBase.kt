package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.github.trcdevelopers.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.util.ResourceLocation
import org.jetbrains.annotations.MustBeInvokedByOverriders
import kotlin.collections.average
import kotlin.collections.forEach
import kotlin.math.floor


abstract class MultiblockControllerBase(
    metaTileEntityId: ResourceLocation,
    tier: Int,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
    recipeRegistry: RecipeRegistry<*>,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey, recipeRegistry) {

    protected val multiblockParts = mutableListOf<IMultiblockPart>()
    var structureFormed = false
        protected set

    abstract fun isConstructed(): Boolean
    abstract override val workable: MultiblockRecipeLogic

    open fun onConstructed() {}

    @MustBeInvokedByOverriders
    open fun onDeconstructed() {
        multiblockParts.forEach { it.removeFromMultiblock(this) }
        multiblockParts.clear()
    }

    override fun update() {
        super.update()
        if (world?.isRemote == true) return
        if (offsetTimer % 20 == 0L) {
            val constructed = isConstructed()
            if (constructed != structureFormed) {
                if (constructed) {
                    onConstructed()
                } else {
                    onDeconstructed()
                }
                structureFormed = constructed
            }
        }
    }

    protected fun calcTier(tiers: Collection<Int>): Int {
        return floor(tiers.average()).toInt()
    }
}