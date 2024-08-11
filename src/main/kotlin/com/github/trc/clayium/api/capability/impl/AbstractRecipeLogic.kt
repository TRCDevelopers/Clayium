package com.github.trc.clayium.api.capability.impl

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.AbstractWorkable
import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.IControllable
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import com.github.trc.clayium.common.util.TransferUtils
import com.github.trc.clayium.integration.jei.JeiPlugin
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
 * Recipe-based implementation of [AbstractWorkable]
 */
abstract class AbstractRecipeLogic(
    metaTileEntity: MetaTileEntity,
    val recipeRegistry: RecipeRegistry<*>,
) : AbstractWorkable(metaTileEntity), IControllable {

    protected val inputInventory = metaTileEntity.importItems

    protected var previousRecipe: Recipe? = null
    protected var recipeCEt = ClayEnergy.ZERO
    // item stacks that will be produced when the recipe is done
    protected var itemOutputs: List<ItemStack> = emptyList()

    /**
     * Draw energy from the energy container
     * @param ce the Clay Energy to remove
     * @param simulate whether to simulate energy extraction or not, default is false
     * @return true if energy can/was drained, otherwise false
     */
    protected abstract fun drawEnergy(ce: ClayEnergy, simulate: Boolean = false): Boolean

    override fun showRecipesInJei() {
        JeiPlugin.jeiRuntime.recipesGui.showCategories(listOf(this.recipeRegistry.category.uniqueId))
    }

    override fun updateWorkingProgress() {
        if (drawEnergy(recipeCEt)) currentProgress++
        if (currentProgress > requiredProgress) {
            completeWork()
        }
    }

    override fun completeWork() {
        currentProgress = 0
        TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs)
    }

    override fun trySearchNewRecipe() {
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
}