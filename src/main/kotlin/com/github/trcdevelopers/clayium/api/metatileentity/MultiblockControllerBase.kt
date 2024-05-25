package com.github.trcdevelopers.clayium.api.metatileentity

import com.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trcdevelopers.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.IMultiblockPart
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.util.ResourceLocation
import org.jetbrains.annotations.MustBeInvokedByOverriders
import kotlin.math.floor


abstract class MultiblockControllerBase(
    metaTileEntityId: ResourceLocation,
    tier: Int,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
    protected val recipeRegistry: RecipeRegistry<*>,
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey) {

    protected val multiblockParts = mutableListOf<IMultiblockPart>()
    val clayEnergyHolder = ClayEnergyHolder(this)
    var structureFormed = false
        protected set

    abstract fun isConstructed(): Boolean
    protected abstract val workable: MultiblockRecipeLogic

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