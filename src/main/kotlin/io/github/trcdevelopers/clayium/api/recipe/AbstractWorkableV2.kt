package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.capability.IWorkingControllableV2
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.util.toList
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation

open class AbstractWorkableV2(
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
            this.recipeProcessor.reset()
        }
    }

    /**
     * Recipe is already matched (inputs are already checked)
     */
    protected open fun consumeInputs(recipe: IClayiumRecipe) {
        val inputInventory = this.recipeProvider.inputInventory
        for (ingredient in recipe.inputs) {
            if (!ingredient.isConsumable) continue
            for (i in 0..<inputInventory.slots) {
                val stack = inputInventory.getStackInSlot(i)
                if (ingredient.testItemStackAndAmount(stack)) {
                    inputInventory.extractItem(i, ingredient.consumeAmount, false)
                    break
                }
            }
        }
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