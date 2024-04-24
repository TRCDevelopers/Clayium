package com.github.trcdevelopers.clayium.common.tileentity.trait

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.tileentity.TileEntityMachine
import com.github.trcdevelopers.clayium.common.util.CUtils
import com.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class BasicRecipeLogic(
    tileEntityMachine: TileEntityMachine,
    tier: Int,
    private val ceSlot: ClayEnergyHolder,
    private val recipeRegistry: RecipeRegistry<*>,
) : TETrait(tileEntityMachine, tier) {

    private val inputInventory = tileEntity.inputInventory

    private var previousRecipe: Recipe? = null
    private var recipeCEt = ClayEnergy.ZERO
    private var requiredProgress = 0
    private var currentProgress = 0
    // item stacks that will be produced when the recipe is done
    private var itemOutputs: List<ItemStack> = emptyList()

    private var invalidInputsForRecipes = false
    private var outputsFull = false

    override fun update() {
        if (tileEntity.world?.isRemote == true) return
        if (currentProgress != 0) {
            updateRecipeProgress()
        }
        if (currentProgress == 0 && shouldSearchNewRecipe()) {
            trySearchNewRecipe()
        }
    }

    private fun updateRecipeProgress() {
        if (ceSlot.drawEnergy(recipeCEt)) currentProgress++
        if (currentProgress > requiredProgress) {
            currentProgress = 0
            TransferUtils.insertToHandler(tileEntity.outputInventory, itemOutputs)
        }
    }

    private fun shouldSearchNewRecipe(): Boolean {
        return canWorkWithInputs() && canFitNewOutputs()
    }

    private fun canWorkWithInputs(): Boolean {
        if (invalidInputsForRecipes && !tileEntity.hasNotifiedInputs) return false

        invalidInputsForRecipes = false
        tileEntity.hasNotifiedInputs = false
        return true
    }

    private fun canFitNewOutputs(): Boolean {
        if (outputsFull && !tileEntity.hasNotifiedOutputs) return false

        outputsFull = false
        tileEntity.hasNotifiedOutputs = false
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
        if (!this.ceSlot.hasEnoughEnergy(recipe.cePerTick)) return
        val outputLimit = tileEntity.outputInventory.slots
        val outputs = recipe.copyOutputs().subList(0, outputLimit)
        if (!TransferUtils.insertToHandler(tileEntity.outputInventory, outputs, true)) {
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

    fun getProgressBar(): ProgressWidget {
        return ProgressWidget()
            .size(22, 17)
            .align(Alignment.Center)
            .progress(this::getNormalizedProgress)
            .texture(ClayGuiTextures.PROGRESS_BAR, 22)
    }

    fun getNormalizedProgress(): Double {
        return currentProgress.toDouble() / requiredProgress.toDouble()
    }

    fun syncValues(syncManager: GuiSyncManager) {
        syncManager.syncValue("requiredProgress", 0, SyncHandlers.intNumber(
            { requiredProgress },
            { rProgress -> requiredProgress = rProgress }
        ))
        syncManager.syncValue("craftingProgress", 1, SyncHandlers.intNumber(
            { currentProgress },
            { cProgress -> currentProgress = cProgress }
        ))
    }

    override fun writeToNBT(data: NBTTagCompound) {
        data.setInteger("currentProgress", currentProgress)
        data.setInteger("requiredProgress", requiredProgress)
        CUtils.writeItems(itemOutputs, "itemOutputs", data)
        data.setLong("recipeCEt", recipeCEt.energy)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        currentProgress = data.getInteger("currentProgress")
        requiredProgress = data.getInteger("requiredProgress")
        itemOutputs = mutableListOf<ItemStack>().apply { CUtils.readItems(this, "itemOutputs", data) }
        recipeCEt = ClayEnergy(data.getLong("recipeCEt"))
    }
}