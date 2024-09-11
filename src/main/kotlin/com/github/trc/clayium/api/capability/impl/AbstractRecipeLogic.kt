package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.AbstractWorkable
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.toList
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
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
    val recipeRegistry: RecipeRegistry<*>,
) : AbstractWorkable(metaTileEntity) {

    protected val inputInventory = metaTileEntity.importItems

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
        JeiPlugin.jeiRuntime.recipesGui.showCategories(listOf(this.recipeRegistry.category.uniqueId))
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
            recipeRegistry.findRecipe(getTier(), inputInventory.toList())
        }

        if (currentRecipe == null) {
            invalidInputsForRecipes = true
            return
        }
        prepareRecipe(currentRecipe)
    }

    protected open fun prepareRecipe(recipe: Recipe) {
        if (!this.drawEnergy(recipe.cePerTick, simulate = true)) return
        val outputs = recipe.copyOutputs().take(metaTileEntity.exportItems.slots)
        if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, outputs, true)) {
            this.outputsFull = true
            return
        }
        if (!recipe.matches(true, inputInventory, getTier())) return
        val (cePerTick, duration) = applyOverclock(recipe.cePerTick, recipe.duration, ocHandler.compensatedFactor)
        this.itemOutputs = outputs
        this.recipeCEt = ClayEnergy(cePerTick)
        this.requiredProgress = duration
        this.currentProgress = 1
        this.previousRecipe = recipe
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
        CUtils.writeItems(itemOutputs, "itemOutputs", data)
        data.setLong("recipeCEt", recipeCEt.energy)
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        itemOutputs = CUtils.readItems("itemOutputs", data)
        recipeCEt = ClayEnergy(data.getLong("recipeCEt"))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ClayiumTileCapabilities.RECIPE_LOGIC) {
            capability.cast(this)
        } else {
            super.getCapability(capability, facing)
        }
    }
}