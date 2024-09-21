package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.AbstractWorkable
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.recipe.IRecipeProvider
import com.github.trc.clayium.api.util.toList
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.util.TransferUtils
import com.github.trc.clayium.integration.jei.JeiPlugin
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import kotlin.math.pow

/**
 * Recipe-based implementation of [AbstractWorkable]
 */
abstract class AbstractRecipeLogic(
    metaTileEntity: MetaTileEntity,
    val recipeProvider: IRecipeProvider,
) : AbstractWorkable(metaTileEntity) {

    protected val inputInventory = metaTileEntity.importItems

    override var isWorking: Boolean = false

    protected var previousRecipe: Recipe? = null
    var recipeCEt = ClayEnergy.ZERO
        protected set

    /**
     * Draw energy from the energy container.
     * Overclocking should be applied.
     * @param ce Clay Energy to remove
     * @param simulate whether to simulate energy extraction or not, default is false
     * @return true if energy can/was drained, otherwise false
     */
    protected abstract fun drawEnergy(ce: ClayEnergy, simulate: Boolean = false): Boolean

    override fun showRecipesInJei() {
        val categories = recipeProvider.jeiCategories
        if (categories.isNotEmpty()) {
            JeiPlugin.jeiRuntime.recipesGui.showCategories(categories)
        }
    }

    final override fun updateWorkingProgress() {
        if (!drawEnergy(recipeCEt)) return
        super.updateWorkingProgress()
    }

    override fun trySearchNewRecipe() {
        var currentRecipe: Recipe? = null
        currentRecipe = if (previousRecipe?.matches(false, inputInventory, getTier()) == true) {
            previousRecipe
        } else {
            recipeProvider.searchRecipe(getTier(), inputInventory.toList())
        }

        if (currentRecipe == null) {
            invalidInputsForRecipes = true
            this.isWorking = false
            return
        }
        this.isWorking = prepareRecipe(currentRecipe)
    }

    protected open fun prepareRecipe(recipe: Recipe): Boolean {
        if (!this.drawEnergy(recipe.cePerTick, simulate = true)) return false
        val outputs = recipe.copyOutputs().take(metaTileEntity.exportItems.slots)
        if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, outputs, true)) {
            this.outputsFull = true
            return false
        }
        if (!recipe.matches(true, inputInventory, getTier())) return false
        val (cePerTick, duration) = applyOverclock(recipe.cePerTick, recipe.duration, ocHandler.compensatedFactor)
        this.itemOutputs = outputs
        this.recipeCEt = ClayEnergy(cePerTick)
        this.requiredProgress = duration
        this.currentProgress = 1
        this.previousRecipe = recipe
        return true
    }

    /**
     * Applies overclock to the recipe.
     * @return { RawCEt, duration }
     */
    protected open fun applyOverclock(cePt: ClayEnergy, duration: Long, compensatedFactor: Double): LongArray {
        val rawCEt = cePt.energy * compensatedFactor.pow(1.5)
        val durationOCed = (duration / compensatedFactor)
        return longArrayOf(rawCEt.toLong(), durationOCed.toLong())
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = super.serializeNBT()
        data.setLong("recipeCEt", recipeCEt.energy)
        data.setBoolean("isWorking", isWorking)
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        recipeCEt = ClayEnergy(data.getLong("recipeCEt"))
        isWorking = data.getBoolean("isWorking")
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ClayiumTileCapabilities.RECIPE_LOGIC) {
            capability.cast(this)
        } else {
            super.getCapability(capability, facing)
        }
    }
}