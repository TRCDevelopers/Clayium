package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.capability.IWorkingControllableV2
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.util.toList
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation

abstract class AbstractWorkableV2(
    metaTileEntity: MetaTileEntity,
    private val recipeProcessor: IRecipeProcessor,
    private val recipeProvider: IRecipeProviderV2,
) : MTETrait(metaTileEntity, "workable_v2"), IWorkingControllableV2 {

    private val inputItemInventory = metaTileEntity.importItems
    private val outputItemInventory = metaTileEntity.exportItems

    protected var recipeOutput: IRecipeOutputs? = null

    override fun update() {
        if (this.metaTileEntity.world?.isRemote == true) return

        if (!this.recipeProcessor.hasRecipe) {
            val newRecipe = this.recipeProvider.searchRecipe(
                this.metaTileEntity.tier.numeric,
                this.inputItemInventory.toList()
            )
            if (newRecipe != null && newRecipe.outputs.canFit(this.outputItemInventory)) {
                this.recipeProcessor.set(newRecipe)
                this.recipeOutput = newRecipe.outputs
            }
        }

        this.recipeProcessor.tick()

        if (this.recipeProcessor.isCompleted) {
            this.recipeOutput?.produceOutputs(this.outputItemInventory)
            this.recipeProcessor.reset()
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