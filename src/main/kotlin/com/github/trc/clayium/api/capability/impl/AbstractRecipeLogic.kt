package com.github.trc.clayium.api.capability.impl

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.IControllable
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.clayenergy.ClayEnergy
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import com.github.trc.clayium.common.util.TransferUtils
import com.github.trc.clayium.integration.jei.JeiPlugin
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

abstract class AbstractRecipeLogic(
    metaTileEntity: MetaTileEntity,
    val recipeRegistry: RecipeRegistry<*>,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.RECIPE_LOGIC), IControllable {

    protected val inputInventory = metaTileEntity.importItems

    protected var previousRecipe: Recipe? = null
    protected var recipeCEt = ClayEnergy.ZERO
    var requiredProgress = 0L
        protected set
    var currentProgress = 0L
        protected set
    // item stacks that will be produced when the recipe is done
    protected var itemOutputs: List<ItemStack> = emptyList()

    private var invalidInputsForRecipes = false
    protected var outputsFull = false

    override val isWorking: Boolean get() = currentProgress != 0L
    override var isWorkingEnabled: Boolean = true
        set(value) {
            field = value
            metaTileEntity.markDirty()
        }

    /**
     * Draw energy from the energy container
     * @param ce the Clay Energy to remove
     * @param simulate whether to simulate energy extraction or not, default is false
     * @return true if energy can/was drained, otherwise false
     */
    protected abstract fun drawEnergy(ce: ClayEnergy, simulate: Boolean = false): Boolean

    override fun update() {
        if (metaTileEntity.world?.isRemote == true) return
        if (!isWorkingEnabled) return
        if (currentProgress != 0L) {
            updateRecipeProgress()
        }
        if (currentProgress == 0L && shouldSearchNewRecipe()) {
            trySearchNewRecipe()
        }
    }

    protected open fun updateRecipeProgress() {
        if (drawEnergy(recipeCEt)) currentProgress++
        if (currentProgress > requiredProgress) {
            completeRecipe()
        }
    }

    protected open fun completeRecipe() {
        currentProgress = 0
        TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs)
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

    protected open fun trySearchNewRecipe() {
        var currentRecipe: Recipe? = null
        currentRecipe = if (previousRecipe?.matches(false, inputInventory, tierNum) == true) {
            previousRecipe
        } else {
            recipeRegistry.findRecipe(tierNum, CUtils.handlerToList(inputInventory))
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
        if (!recipe.matches(true, inputInventory, tierNum)) return
        this.itemOutputs = outputs
        this.recipeCEt = recipe.cePerTick
        this.requiredProgress = recipe.duration
        this.currentProgress = 1
        this.previousRecipe = recipe
    }

    fun getProgressBar(syncManager: GuiSyncManager): ProgressWidget {
        syncManager.syncValue("requiredProgress", SyncHandlers.longNumber(
            { requiredProgress },
            { rProgress -> requiredProgress = rProgress }
        ))
        syncManager.syncValue("craftingProgress", SyncHandlers.longNumber(
            { currentProgress },
            { cProgress -> currentProgress = cProgress }
        ))

        val widget = ProgressWidget()
            .size(22, 17)
            .align(Alignment.Center)
            .progress(this::getNormalizedProgress)
            .texture(ClayGuiTextures.PROGRESS_BAR, 22)
            if (Mods.JustEnoughItems.isModLoaded) {
                widget.addTooltipLine(IKey.lang("jei.tooltip.show.recipes"))
                    .listenGuiAction(IGuiAction.MousePressed { _ ->
                        if (!widget.isBelowMouse) return@MousePressed false
                        JeiPlugin.jeiRuntime.recipesGui.showCategories(listOf(this@AbstractRecipeLogic.recipeRegistry.category.uniqueId))
                        return@MousePressed true
                })
            }

        return widget
    }

    fun getNormalizedProgress(): Double {
        if (currentProgress == 0L || requiredProgress == 0L) return 0.0
        return (currentProgress.toDouble() - 1.0) / requiredProgress.toDouble()
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = super.serializeNBT()
        data.setLong("currentProgress", currentProgress)
        data.setLong("requiredProgress", requiredProgress)
        CUtils.writeItems(itemOutputs, "itemOutputs", data)
        data.setLong("recipeCEt", recipeCEt.energy)
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        currentProgress = data.getLong("currentProgress")
        requiredProgress = data.getLong("requiredProgress")
        itemOutputs = CUtils.readItems("itemOutputs", data)
        recipeCEt = ClayEnergy(data.getLong("recipeCEt"))
    }
}