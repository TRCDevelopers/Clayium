package io.github.trcdevelopers.clayium.api.recipe

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.ProgressWidget
import io.github.trcdevelopers.clayium.api.capability.IWorkingControllableV2
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import io.github.trcdevelopers.clayium.integration.jei.JeiPlugin
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation

open class WorkableV2(
    metaTileEntity: MetaTileEntity,
    private val recipeProcessor: IRecipeProcessor,
    private val recipeProvider: RecipeProvider,
) : MTETrait(metaTileEntity, "workable_v2"), IWorkingControllableV2 {

    override var isWorkingEnabled: Boolean = true

    protected var recipeOutput: IRecipeOutputs? = null

    override fun update() {
        if (this.metaTileEntity.world?.isRemote == true) return
        if (!this.isWorkingEnabled) return

        if (!this.recipeProcessor.hasRecipe) {
            val recipe = this.recipeProvider.searchNewRecipe(this.metaTileEntity.tier.numeric)
            if (recipe != null) {
                this.recipeOutput = recipe.outputs
                this.recipeProcessor.set(recipe)
                this.consumeInputs(recipe)
            }
        }

        this.recipeProcessor.tick()

        if (this.recipeProcessor.isCompleted) {
            this.recipeOutput?.produceOutputs(this.recipeProvider.outputInventory)
            this.recipeOutput = null
            this.recipeProcessor.reset()
        }
    }

    /**
     * Recipe is already matched (inputs are already checked)
     */
    protected open fun consumeInputs(recipe: IClayiumRecipe) {
        val inputInventory = this.recipeProvider.inputInventory
        for (ingredient in recipe.itemInputs) {
            if (!ingredient.isConsumable) continue
            for (i in 0..<inputInventory.slots) {
                val stack = inputInventory.getStackInSlot(i)
                if (ingredient.test(stack)) {
                    inputInventory.extractItem(i, ingredient.consumeAmount, false)
                    break
                }
            }
        }
    }

    fun progressBar(syncManager: PanelSyncManager, showRecipes: Boolean = true): ProgressWidget {
        syncManager.syncValue("requiredProgress", SyncHandlers.longNumber(recipeProcessor::requiredProgress, recipeProcessor::requiredProgress::set))
        syncManager.syncValue("craftingProgress", SyncHandlers.longNumber(recipeProcessor::currentProgress, recipeProcessor::currentProgress::set))

        val widget = ProgressWidget()
            .size(22, 17)
            .progress(recipeProcessor::normalizedProgress)
            .texture(ClayGuiTextures.PROGRESS_BAR, 22)
        if (showRecipes && Mods.JustEnoughItems.isModLoaded) {
            widget.addTooltipLine(IKey.lang("jei.tooltip.show.recipes"))
                .listenGuiAction(IGuiAction.MousePressed { _ ->
                    if (!widget.isBelowMouse) return@MousePressed false
                    val categories = recipeProvider.registry.jeiCategories
                    if (categories.isNotEmpty()) {
                        JeiPlugin.jeiRuntime.recipesGui.showCategories(categories)
                    }
                    return@MousePressed true
                })
        }

        return widget
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = super.serializeNBT()
        recipeOutput?.let { data.setTag("recipe_output", it.serializeNBT()) }
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        if (data.hasKey("recipe_output") && data.hasKey("recipe_output_type")) {
            val id = ResourceLocation(data.getString("recipe_output_type"))
            val output = CRecipes.getRecipeOutput(id)
            if (output != null) {
                output.deserializeNBT(data.getCompoundTag("recipe_output"))
                this.recipeOutput = output
            }
        }
    }
}