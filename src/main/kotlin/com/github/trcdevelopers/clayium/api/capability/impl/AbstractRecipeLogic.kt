package com.github.trcdevelopers.clayium.api.capability.impl

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import com.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

abstract class AbstractRecipeLogic(
    metaTileEntity: MetaTileEntity,
    private val recipeRegistry: RecipeRegistry<*>,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.RECIPE_LOGIC) {

    private val inputInventory = metaTileEntity.importItems

    private var previousRecipe: Recipe? = null
    private var recipeCEt = ClayEnergy.ZERO
    private var requiredProgress = 0
    private var currentProgress = 0
    // item stacks that will be produced when the recipe is done
    private var itemOutputs: List<ItemStack> = emptyList()

    private var invalidInputsForRecipes = false
    private var outputsFull = false

    /**
     * Draw energy from the energy container
     * @param ce the Clay Energy to remove
     * @param whether to simulate energy extraction or not, default is false
     * @return true if energy can/was drained, otherwise false
     */
    protected abstract fun drawEnergy(ce: ClayEnergy, simulate: Boolean = false): Boolean

    override fun update() {
        if (metaTileEntity.world?.isRemote == true) return
        if (currentProgress != 0) {
            updateRecipeProgress()
        }
        if (currentProgress == 0 && shouldSearchNewRecipe()) {
            trySearchNewRecipe()
        }
    }

    private fun updateRecipeProgress() {
        if (drawEnergy(recipeCEt)) currentProgress++
        if (currentProgress > requiredProgress) {
            currentProgress = 0
            TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs)
        }
    }

    private fun shouldSearchNewRecipe(): Boolean {
        return canWorkWithInputs() && canFitNewOutputs()
    }

    private fun canWorkWithInputs(): Boolean {
        if (invalidInputsForRecipes && !metaTileEntity.hasNotifiedInputs) return false

        invalidInputsForRecipes = false
        metaTileEntity.hasNotifiedInputs = false
        return true
    }

    private fun canFitNewOutputs(): Boolean {
        if (outputsFull && !metaTileEntity.hasNotifiedOutputs) return false

        outputsFull = false
        metaTileEntity.hasNotifiedOutputs = false
        return true
    }

    private fun trySearchNewRecipe() {
        var currentRecipe: Recipe? = null
        currentRecipe = if (previousRecipe?.matches(false, inputInventory, tier) == true) {
            previousRecipe
        } else {
            recipeRegistry.findRecipe(tier, CUtils.handlerToList(inputInventory))
        }

        if (currentRecipe == null) {
            invalidInputsForRecipes = true
            return
        }
        prepareRecipe(currentRecipe)
    }

    private fun prepareRecipe(recipe: Recipe) {
        if (!this.drawEnergy(this.recipeCEt, simulate = true)) return
        val outputLimit = metaTileEntity.exportItems.slots
        val outputs = recipe.copyOutputs().subList(0, outputLimit)
        if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, outputs, true)) {
            this.outputsFull = true
            return
        }
        if (!recipe.matches(true, inputInventory, tier)) return
        this.itemOutputs = outputs
        this.recipeCEt = recipe.cePerTick
        this.requiredProgress = recipe.duration
        this.currentProgress = 1
        this.previousRecipe = recipe
    }

    fun getProgressBar(syncManager: GuiSyncManager): ProgressWidget {
        syncManager.syncValue("requiredProgress", 0, SyncHandlers.intNumber(
            { requiredProgress },
            { rProgress -> requiredProgress = rProgress }
        ))
        syncManager.syncValue("craftingProgress", 1, SyncHandlers.intNumber(
            { currentProgress },
            { cProgress -> currentProgress = cProgress }
        ))

        return ProgressWidget()
            .size(22, 17)
            .align(Alignment.Center)
            .progress(this::getNormalizedProgress)
            .texture(ClayGuiTextures.PROGRESS_BAR, 22)
    }

    fun getNormalizedProgress(): Double {
        return currentProgress.toDouble() / requiredProgress.toDouble()
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = super.serializeNBT()
        data.setInteger("currentProgress", currentProgress)
        data.setInteger("requiredProgress", requiredProgress)
        CUtils.writeItems(itemOutputs, "itemOutputs", data)
        data.setLong("recipeCEt", recipeCEt.energy)
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        currentProgress = data.getInteger("currentProgress")
        requiredProgress = data.getInteger("requiredProgress")
        itemOutputs = mutableListOf<ItemStack>().apply { CUtils.readItems(this, "itemOutputs", data) }
        recipeCEt = ClayEnergy(data.getLong("recipeCEt"))
    }
}